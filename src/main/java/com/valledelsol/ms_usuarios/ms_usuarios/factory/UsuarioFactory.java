package com.valledelsol.ms_usuarios.ms_usuarios.factory;

import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;

public class UsuarioFactory {
    public static Usuario crearUsuarioPorRol(String email, String password, String rol) {
        return Usuario.builder()
                .email(email)
                .password(password)
                .rol(rol.toUpperCase())
                .build();
    }
}