package com.valledelsol.ms_usuarios.ms_usuarios.service;

import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;
import com.valledelsol.ms_usuarios.ms_usuarios.repository.UsuarioRepository;
import com.valledelsol.ms_usuarios.ms_usuarios.dto.LoginRequest;
import com.valledelsol.ms_usuarios.ms_usuarios.security.JwtUtils;
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

    public String login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return jwtUtils.generateToken(usuario.getEmail());
        } else {
            throw new RuntimeException("Credenciales incorrectas");
        }
    }
}