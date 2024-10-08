package com.taxilf.core.model.dto.request;

import com.taxilf.core.utility.Variables;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = Variables.PHONE_REGEX, message = "Invalid phone number")
    private String phone;

}
