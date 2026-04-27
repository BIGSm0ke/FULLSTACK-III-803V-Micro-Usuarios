package com.valledelsol.ms_usuarios.ms_usuarios.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
}