package com.taxilf.core.model.dto;

import com.taxilf.core.utility.Variables;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank
    @Pattern(regexp = Variables.USER_DTO_ROLES)
    private String role;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Variables.PHONE_REGEX, message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = Variables.OTP_REGEX, message = "Invalid Code")
    private String code;

}
