package com.taxilf.core.utility;

import java.util.Random;

public class EncryptionUtils {

    /*
    * A very simple way to change the display value in JWT (ID)
    * It can be much more complicated with using algorithms and stuff like that
    */
    
    private static final int JUST_A_RANDOM_NUMBER = 234092;

    public static String encode(long id){
        return String.valueOf(id + JUST_A_RANDOM_NUMBER);
    }

    public static Long decode(String id){
        return Long.parseLong(id) - JUST_A_RANDOM_NUMBER;
    }

    public static String otp(){
        return String.format("%06d", new Random().nextInt(111111,1000000));
    }
}
