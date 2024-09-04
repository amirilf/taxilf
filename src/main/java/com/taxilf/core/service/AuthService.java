package com.taxilf.core.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomTooManyRequestException;
import com.taxilf.core.exception.CustomUnauthorizedException;
import com.taxilf.core.model.dto.LoginDTO;
import com.taxilf.core.model.dto.RegisterDTO;
import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.enums.Gender;
import com.taxilf.core.model.enums.Role;
import com.taxilf.core.model.enums.UserStatus;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.utility.EncryptionUtils;
import com.taxilf.core.utility.Variables;

@Service
public class AuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final JwtService jwtService;

    AuthService(RedisTemplate<String, Object> redisTemplate, PassengerRepository passengerRepository, DriverRepository driverRepository, JwtService jwtService){
        this.redisTemplate = redisTemplate;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<String> register(RegisterDTO registerDTO) {

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            throw new CustomBadRequestException("User is already logged in, first logout.");
        }
        
        Long id;
        String name = registerDTO.getName();
        String gender = registerDTO.getGender();
        String phone = registerDTO.getPhone();
        String code = registerDTO.getCode();
        String role = registerDTO.getRole();

        // check otp
        otpCheck(phone, code);

        if (role.equals(Role.PASSENGER.name())) {
            
            if (passengerRepository.existsByPhone(phone)) {
                throw new CustomBadRequestException("Phone number is already registered.");
            }
            
            Passenger passenger = Passenger.builder()
                .name(name)
                .phone(phone)
                .gender(gender != null ? Gender.valueOf(gender) : null)
                .status(UserStatus.NONE)
                .role(Role.PASSENGER)
                .build();

            passengerRepository.save(passenger);
            id = passenger.getId();  
        
        } else if (role.equals(Role.DRIVER.name())) {
            
            if (driverRepository.existsByPhone(phone)) {
                throw new CustomBadRequestException("Phone number is already registered.");
            }

            Driver driver = Driver.builder()
                .name(name)
                .phone(phone)
                .gender(gender != null ? Gender.valueOf(gender) : null)
                .status(UserStatus.NONE)
                .role(Role.DRIVER)
                .build();

            driverRepository.save(driver);
            id = driver.getId();

        } else {
            // Admin role
            throw new CustomBadRequestException("Admin registration is not yet supported.");
        }

        String token = jwtService.generateToken(id, role);
        return ResponseEntity.ok().body(role.toLowerCase() + " is successfully created\n" + token);
    }


    public ResponseEntity<String> login(LoginDTO loginDTO) {

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            throw new CustomBadRequestException("User is already logged in, first logout.");
        }

        Long id;
        String phone = loginDTO.getPhone();
        String code = loginDTO.getCode();
        String role = loginDTO.getRole();

        // check otp
        otpCheck(phone, code);

        if (role.equals(Role.PASSENGER.name())) {
            id = passengerRepository.findIdByPhone(phone);
        } else if (role.equals(Role.DRIVER.name())) {
            id = driverRepository.findIdByPhone(phone);
        } else {
            // Admin role
            throw new CustomBadRequestException("Admin authentication is not yet supported.");
        }

        if (id == null) {
            throw new CustomBadRequestException("Phone number is not registered.");
        }
        
        // generate access token
        String token = jwtService.generateToken(id, role);
        return ResponseEntity.ok("successful " + role.toLowerCase() + " login\n" + token);
    }

    public ResponseEntity<String> requestOTP(String phone) {
        
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
        String otp = EncryptionUtils.otp();
        ops.set(otpKey, otp, Variables.OTP_TTL_PER_SECONDS, TimeUnit.SECONDS);
        System.out.println("The code: " + otp); // calling sendSMS() method in real world
    
        return ResponseEntity.ok("OTP is sent");
    }

    private void otpCheck(String phone, String code) {

        String otpKey = Variables.OTP_PREF + phone;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String storedOtp = (String) ops.get(otpKey);

        if (storedOtp == null || !storedOtp.equals(code)) {
            throw new CustomUnauthorizedException("Invalid OTP");
        }
    }
}
