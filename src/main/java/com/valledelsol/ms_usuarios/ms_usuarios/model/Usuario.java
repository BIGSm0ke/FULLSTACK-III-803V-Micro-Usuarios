package com.valledelsol.ms_usuarios.ms_usuarios.model;

import jakarta.persistence.*;
import lombok.*;
// IMPORTANTE: Estos imports son necesarios para UserDetails
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails { // <--- Paso 1: Agregar el "implements"

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nombre;
    private String apellido;

    @Column(nullable = false)
    private String rol;

    private String telefono;

    // --- PASO 2: Implementar los métodos obligatorios de UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Esto le dice a Spring Security qué permisos tiene el usuario.
        // Se le agrega el prefijo "ROLE_" por estándar de Spring.
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getUsername() {
        // Spring Security necesita un "nombre de usuario". En tu caso es el email.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // La cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // La cuenta no está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // El usuario está activo
    }
}