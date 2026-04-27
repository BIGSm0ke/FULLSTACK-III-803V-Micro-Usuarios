package com.valledelsol.ms_usuarios.ms_usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Método clave para la autenticación
    Optional<Usuario> findByEmail(String email);
}