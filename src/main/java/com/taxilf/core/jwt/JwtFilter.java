package com.taxilf.core.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        try {

            String header = request.getHeader("Authorization");
            
            if (header != null && header.startsWith("Bearer ")) {
                
                String token = header.substring(7);
                
                Map<String, String> claims = jwtService.extractRoleAndID(token);
                
                String role = (String) claims.get("role");
                String id = claims.get("id");
                
                if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    UserDetails userDetails = User
                        .withUsername(id)
                        .password("")
                        .authorities(new SimpleGrantedAuthority(role))
                        .build();
                    
                        if (jwtService.validateToken(token)) {
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
                
            filterChain.doFilter(request, response);

        } catch (SignatureException ex) {
            handleException(request, response, "Invalid JWT signature", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException ex) {
            handleException(request, response, "Malformed JWT token", HttpServletResponse.SC_BAD_REQUEST);
        } catch (ExpiredJwtException ex) {
            handleException(request, response, "Expired JWT token", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (UnsupportedJwtException ex) {
            handleException(request, response, "Unsupported JWT token", HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException ex) {
            handleException(request, response, "JWT token is missing or invalid", HttpServletResponse.SC_BAD_REQUEST);
        } catch (JwtException ex) {
            handleException(request, response, "Invalid JWT token", HttpServletResponse.SC_UNAUTHORIZED);
        }   
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, String message, int status) throws IOException {
        
        String result = String.format(
                "{\"path\": \"%s\", \"error\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\", \"status\": %d}",
                request.getRequestURI(), HttpStatus.valueOf(status).getReasonPhrase(), message, LocalDateTime.now(), status
        );

        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(result);
    }
}
