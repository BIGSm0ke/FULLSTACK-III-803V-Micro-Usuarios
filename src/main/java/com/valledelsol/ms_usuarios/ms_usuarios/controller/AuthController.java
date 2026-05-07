package com.valledelsol.ms_usuarios.ms_usuarios.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.valledelsol.ms_usuarios.ms_usuarios.dto.LoginRequest;
import com.valledelsol.ms_usuarios.ms_usuarios.dto.UsuarioDTO;
import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;
import com.valledelsol.ms_usuarios.ms_usuarios.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, Object> body) {
        Usuario usuario = new Usuario();
        usuario.setEmail((String) body.getOrDefault("email", ""));
        usuario.setPassword((String) body.getOrDefault("password", ""));
        usuario.setNombre((String) body.getOrDefault("name", (String) body.getOrDefault("nombre", "")));
        usuario.setApellido((String) body.getOrDefault("apellido", ""));
        usuario.setTelefono((String) body.getOrDefault("phone", (String) body.getOrDefault("telefono", "")));
        usuario.setRol((String) body.getOrDefault("rol", "USER"));

        Usuario saved = usuarioService.registrarUsuario(usuario);
        String token = usuarioService.generarToken(saved.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", toDTO(saved));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> dtos = usuarioService.listarTodos()
                .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = usuarioService.login(request);
            Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", toDTO(usuario));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String email = obtenerEmailDesdeRequest(request);
        try {
            Usuario usuario = usuarioService.buscarPorEmail(email);
            return ResponseEntity.ok(toDTO(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonMap("error", "Usuario no encontrado"));
        }
    }

    @PutMapping("/users/profile")
    public ResponseEntity<?> updateProfile(HttpServletRequest servletRequest, @RequestBody Map<String, String> updates) {
        String email = obtenerEmailDesdeRequest(servletRequest);
        try {
            Usuario usuario = usuarioService.buscarPorEmail(email);
            if (updates.containsKey("nombre")) usuario.setNombre(updates.get("nombre"));
            if (updates.containsKey("apellido")) usuario.setApellido(updates.get("apellido"));
            if (updates.containsKey("telefono")) usuario.setTelefono(updates.get("telefono"));
            usuarioService.actualizarUsuario(usuario);
            return ResponseEntity.ok(toDTO(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/auth/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "estado", "UP",
            "servicio", "ms-usuarios"
        ));
    }

    private String obtenerEmailDesdeRequest(HttpServletRequest request) {
        String email = request.getHeader("X-User-Name");
        if (email != null) return email;
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) return auth.getName();
        throw new RuntimeException("No se pudo identificar al usuario");
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setRol(u.getRol());
        dto.setAdmin("ADMIN".equalsIgnoreCase(u.getRol()));
        return dto;
    }
}