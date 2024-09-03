package com.taxilf.core.service;

import org.springframework.stereotype.Service;

import com.taxilf.core.utility.EncryptionUtils;
import com.taxilf.core.utility.Variables;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Base64;
import java.util.function.Function;

@Service
public class JwtService {

    public JwtService(){
        generateSecretKey();
    }

    private void generateSecretKey() {
        try {
            KeyGenerator keyGan = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGan.generateKey();
            Variables.SECRET_KEY = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(Variables.SECRET_KEY));
    }
        
    public String generateToken(Long id,String role){

        Map<String, String> claims = new HashMap<>();
        claims.put("role", role);

        Key key = getKey();

        return Jwts.builder()
            .claims()
            .add(claims)
            .subject(EncryptionUtils.encode(id))
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + Variables.ACCESS_TOKEN_EXPIRATION_TIME))
            .and()
            .signWith(key)
            .compact();
    }

    public <T> T extractClaim(String token,Function<Claims,T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Map<String, String> extractRoleAndID(String token){
        
        Claims claims = extractAllClaims(token);
        
        String id = String.valueOf(EncryptionUtils.decode(claims.getSubject()));
        String role = (String) claims.get("role");

        Map<String, String> result = new HashMap<>();
        
        result.put("id", id);
        result.put("role", role);
        
        return result;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    public boolean validateToken(String token) {
        return extractClaim(token, Claims::getExpiration).after(new Date());
    }

}
