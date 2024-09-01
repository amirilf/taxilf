package com.taxilf.core.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomTooManyRequestException;
import com.taxilf.core.exception.CustomUnauthorizedException;
import com.taxilf.core.model.dto.RegisterPassengerDTO;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.enums.Gender;
import com.taxilf.core.model.entity.enums.Role;
import com.taxilf.core.model.entity.enums.UserStatus;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.utility.Encryption;
import com.taxilf.core.utility.Variables;

@Service
public class AuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PassengerRepository passengerRepository;

    AuthService(RedisTemplate<String, Object> redisTemplate, PassengerRepository passengerRepository){
        this.redisTemplate = redisTemplate;
        this.passengerRepository = passengerRepository;
    }

    public ResponseEntity<String> register(RegisterPassengerDTO registerPassengerDTO) {
        
        String phone = registerPassengerDTO.getPhone();

        if (passengerRepository.existsByPhone(phone)) {
            throw new CustomBadRequestException("Phone number is already registered.");
        }

        String otpKey = Variables.OTP_PREF + phone;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String storedOtp = (String) ops.get(otpKey);

        if (storedOtp == null || !storedOtp.equals(registerPassengerDTO.getCode())) {
            throw new CustomUnauthorizedException("Invalid OTP");
        }

        Passenger passenger = Passenger.builder()
                .name(registerPassengerDTO.getName())
                .phone(phone)
                .gender(registerPassengerDTO.getGender() != null ? Gender.valueOf(registerPassengerDTO.getGender()) : null)
                .status(UserStatus.NONE)
                .role(Role.PASSENGER)
                .build();

        passengerRepository.save(passenger);    

        return ResponseEntity.ok().body("Passenger is successfully created");
    }


    public ResponseEntity<String> login(String phone, String code) {

        if (!passengerRepository.existsByPhone(phone)) {
            throw new CustomBadRequestException("Phone number is not registered.");
        }

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String storedOtp = (String) ops.get(Variables.OTP_PREF + phone);

        if (storedOtp == null || !storedOtp.equals(code)) {
            throw new CustomUnauthorizedException("Invalid OTP");
        }
        
        // generating tokens (access and refresh) here
        return ResponseEntity.ok("Login successful");
    }

    public ResponseEntity<String> otpRequest(String phone) {
        
        String otpKey = Variables.OTP_PREF + phone;
        String limitKey = Variables.OTP_LIMIT_PREF + phone;
        
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
    
        // get limit from redis
        String requestCountStr = (String) ops.get(limitKey);
        Integer requestCount = requestCountStr != null ? Integer.parseInt(requestCountStr) : 0;
        
        // check limitation
        if (requestCount >= Variables.OTP_MAX_REQUEST) {
            throw new CustomTooManyRequestException("Too many requests for OTP. Try again later.");
        }
            
        // increase limitation counter & set expire time of it
        ops.increment(limitKey, 1);
        redisTemplate.expire(limitKey, Variables.OTP_TIME_WINDOW, TimeUnit.SECONDS);
    
        // create otp & set TTL
        String otp = Encryption.otp();
        ops.set(otpKey, otp, Variables.OTP_TTL_PER_SECONDS, TimeUnit.SECONDS);
        System.out.println("The code: " + otp); // calling sendSMS() method in real world
    
        return ResponseEntity.ok("OTP is sent");
    }
}
