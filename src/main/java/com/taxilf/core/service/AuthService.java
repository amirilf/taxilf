package com.taxilf.core.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.utility.Security;
import com.taxilf.core.utility.Variables;

import jakarta.validation.Valid;

@Service
public class AuthService {

    private final RedisTemplate<String, Object> redisTemplate;

    AuthService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }


    public Passenger register(@Valid Passenger user) {
        return null;
    }

    public ResponseEntity<String> login(String phone, String code) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String storedOtp = (String) ops.get("otp_" + phone);

        if (storedOtp != null && storedOtp.equals(code)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(401).body("Invalid OTP");
        }
    }

    public ResponseEntity<String> loginRequest(String phone) {
        
        String otpKey = Variables.OTP_PREF + phone;
        String limitKey = Variables.OTP_LIMIT_PREF + phone;
        
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
    
        // get limit from redis
        String requestCountStr = (String) ops.get(limitKey);
        Integer requestCount = requestCountStr != null ? Integer.parseInt(requestCountStr) : 0;
        
        // check limitation
        if (requestCount >= Variables.OTP_MAX_REQUEST) {
            return ResponseEntity.status(429).body("Too many requests. Please try again later.");
        }
            
        // increase limitation counter & set expire time of it
        ops.increment(limitKey, 1);
        redisTemplate.expire(limitKey, Variables.OTP_TIME_WINDOW, TimeUnit.SECONDS);
    
        // create otp & set TTL
        String otp = Security.otp();
        ops.set(otpKey, otp, Variables.OTP_TTL_PER_SECONDS, TimeUnit.SECONDS);
        System.out.println("The code: " + otp); // calling sendSMS() method in real world
    
        return ResponseEntity.ok("OTP is sent");
    }
    

}
