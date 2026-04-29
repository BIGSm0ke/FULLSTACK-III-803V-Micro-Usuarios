package com.valledelsol.ms_usuarios.ms_usuarios.repository;

import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Crucial para el Login: Spring generará la consulta SQL automáticamente
    Optional<Usuario> findByEmail(String email);
}