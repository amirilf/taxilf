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
import com.taxilf.core.model.dto.request.LoginDTO;
import com.taxilf.core.model.dto.request.RegisterDTO;
import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.entity.Wallet;
import com.taxilf.core.model.enums.Gender;
import com.taxilf.core.model.enums.Role;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.UserRepository;
import com.taxilf.core.utility.EncryptionUtils;
import com.taxilf.core.utility.GeometryUtils;
import com.taxilf.core.utility.Variables;

@Service
public class AuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final JwtService jwtService;

    AuthService(RedisTemplate<String, Object> redisTemplate,UserRepository userRepository, PassengerRepository passengerRepository, DriverRepository driverRepository, JwtService jwtService){
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<String> register(RegisterDTO registerDTO) {

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            throw new CustomBadRequestException("User is already logged in, first logout.");
        }
        
        Long id;
        Long userId;
        String name = registerDTO.getName();
        String gender = registerDTO.getGender();
        String phone = registerDTO.getPhone();
        String code = registerDTO.getCode();
        String role = registerDTO.getRole();

        // check otp
        otpCheck(phone, code);

        if (userRepository.existsByPhone(phone)) {
            throw new CustomBadRequestException("Phone number is already registered.");
        }

        Wallet wallet = new Wallet();
        User user = User.builder()
            .name(name)
            .phone(phone)
            .gender(gender != null ? Gender.valueOf(gender) : Gender.NONE)
            .location(GeometryUtils.randomPointInMashhad())
            .wallet(wallet)
            .build();
        wallet.setUser(user);


        if (role.equals(Role.PASSENGER.name())) {

            user.setRole(Role.PASSENGER);
            
            Passenger passenger = Passenger.builder()
                .user(user)
                .build();

            userRepository.save(user);
            passengerRepository.save(passenger);
            id = passenger.getId();  
            userId = user.getId();
            
        } else if (role.equals(Role.DRIVER.name())) {
            
            user.setRole(Role.DRIVER);

            Driver driver = Driver.builder()
                .user(user)
                .build();

            userRepository.save(user);
            driverRepository.save(driver);
            id = driver.getId();
            userId = user.getId();

        } else {
            // Admin role
            throw new CustomBadRequestException("Admin registration is not yet supported.");
        }

        String token = jwtService.generateToken(id, userId, role);
        return ResponseEntity.ok().body(role.toLowerCase() + " is successfully created\n" + token);
    }


    public ResponseEntity<String> login(LoginDTO loginDTO) {

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            throw new CustomBadRequestException("User is already logged in, first logout.");
        }

        Long id;
        Long userId;
        String phone = loginDTO.getPhone();
        String code = loginDTO.getCode();
        String role = loginDTO.getRole();

        // check otp
        otpCheck(phone, code);

        userId = userRepository.findIdByPhone(phone);

        if (role.equals(Role.PASSENGER.name())) {
            id = passengerRepository.findIdByUserId(userId);
        } else if (role.equals(Role.DRIVER.name())) {
            id = driverRepository.findIdByUserId(userId);
        } else {
            // Admin role
            throw new CustomBadRequestException("Admin authentication is not yet supported.");
        }

        if (id == null || userId == null) {
            throw new CustomBadRequestException("Phone number is not registered.");
        }
        
        // generate access token
        String token = jwtService.generateToken(id, userId, role);
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
