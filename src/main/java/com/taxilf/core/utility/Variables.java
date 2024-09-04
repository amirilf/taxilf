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

    // GEOMETY UTIL
    public static final double EARTH_RADIUS = 6371000; // in meters
    public static final double PRICE_PER_METERS = 0.01; // lets say in $
    public static final double RADIUS_IN_DEGREES = 0.05; // ~5km
    public static final double MASHHAD_LAT = 36.31;
    public static final double MASHHAD_LON = 59.59;

}
