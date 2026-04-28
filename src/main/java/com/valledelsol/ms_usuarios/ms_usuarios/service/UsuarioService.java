package com.valledelsol.ms_usuarios.ms_usuarios.service;

import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;
import com.valledelsol.ms_usuarios.ms_usuarios.repository.UsuarioRepository;
import com.valledelsol.ms_usuarios.ms_usuarios.dto.LoginRequest;
import com.valledelsol.ms_usuarios.ms_usuarios.security.JwtUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // <-- IMPORTANTE: Import del Circuit Breaker
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public Usuario registrarUsuario(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Dejamos un SOLO método login, el que tiene la protección del Circuit Breaker
    @CircuitBreaker(name = "loginCB", fallbackMethod = "fallbackLogin")
    public String login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return jwtUtils.generateToken(usuario.getEmail());
        } else {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    // Método Fallback: se activa si falla la DB, si JwtUtils falla o el sistema está saturado
    public String fallbackLogin(LoginRequest request, Throwable t) {
        return "Servicio de autenticación en modo de espera. Intente más tarde. (Error: " + t.getMessage() + ")";
    }
}
