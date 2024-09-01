package com.taxilf.core.model.dto;

import com.taxilf.core.utility.Variables;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterPassengerDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Variables.PHONE_REGEX, message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = Variables.OTP_REGEX, message = "Invalid Code")
    private String code;

    @Pattern(regexp = "MALE|FEMALE|", message = "Gender must be either MALE or FEMALE")
    private String gender;

}
