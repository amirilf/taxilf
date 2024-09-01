package com.taxilf.core.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "\\d{3}", message = "Invalid phone number")
    private String phone;

}
