package com.taxilf.core.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "\\d{3}", message = "Invalid phone number")
    private String phone;

    @NotEmpty(message = "Code is required")
    @Pattern(regexp = "\\d{6}", message = "Invalid Code")
    private String code;

}
