package com.taxilf.core.utility;

public class Variables {
    
    // Security 
    public static final int ENCODER_STRENGTH = 5;
    
    // Redis
    public static final String OTP_PREF = "otp_";
    public static final String OTP_LIMIT_PREF = OTP_PREF + "limit_";
    
    public static final int OTP_MAX_REQUEST = 3;
    public static final int OTP_TIME_WINDOW = 10;

    public static final int OTP_TTL_PER_SECONDS = 20;

    // Validation
    public static final String PHONE_REGEX = "\\d{3}";
    public static final String OTP_REGEX = "\\d{6}";

    // JWT
    public static String SECRET_KEY;
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 15; // 15 min  

    // DTOs
    public static final String USER_DTO_ROLES = "PASSENGER|DRIVER|ADMIN";
    public static final String USER_DTO_GENDER = "MALE|FEMALE";
}
