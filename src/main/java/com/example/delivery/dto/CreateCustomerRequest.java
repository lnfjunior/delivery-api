package com.example.delivery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateCustomerRequest {
    @NotBlank
    public String name;
    @NotBlank
    @Email
    public String email;
    public String phone;
}
