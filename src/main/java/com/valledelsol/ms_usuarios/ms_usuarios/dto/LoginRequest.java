package com.valledelsol.ms_usuarios.ms_usuarios.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}