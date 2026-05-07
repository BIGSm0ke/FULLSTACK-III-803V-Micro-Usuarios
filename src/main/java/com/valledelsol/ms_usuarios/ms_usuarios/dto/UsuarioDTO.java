package com.valledelsol.ms_usuarios.ms_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;
    private String photo;

    @JsonProperty("isAdmin")
    private boolean admin;

    @JsonProperty("name")
    public String getName() {
        return nombre;
    }

    @JsonProperty("phone")
    public String getPhone() {
        return telefono;
    }
}