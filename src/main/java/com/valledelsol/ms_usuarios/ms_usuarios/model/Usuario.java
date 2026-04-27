package com.valledelsol.ms_usuarios.ms_usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data // Genera getters, setters, toString, equals y hashcode
@Builder // Permite crear objetos de forma fluida: Usuario.builder().nombre("...").build()
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombre;

    private String apellido;

    // Los roles según el informe: CIUDADANO, FUNCIONARIO, BRIGADISTA
    @Column(nullable = false)
    private String rol;

    // Podrías agregar un campo para el teléfono, útil para las Alertas a la Comunidad
    private String telefono;
}