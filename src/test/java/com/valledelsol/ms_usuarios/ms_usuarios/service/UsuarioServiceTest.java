package com.valledelsol.ms_usuarios.ms_usuarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.valledelsol.ms_usuarios.ms_usuarios.dto.LoginRequest;
import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;
import com.valledelsol.ms_usuarios.ms_usuarios.repository.UsuarioRepository;
import com.valledelsol.ms_usuarios.ms_usuarios.security.JwtUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private LoginRequest loginRequestMock;

    @BeforeEach
    void setUp() {
        // Preparamos datos falsos para usar en las pruebas antes de cada test
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("juan@valledelsol.cl");
        usuarioMock.setPassword("passwordEncriptada");
        usuarioMock.setNombre("Juan");
        usuarioMock.setRol("CIUDADANO");

        loginRequestMock = new LoginRequest();
        loginRequestMock.setEmail("juan@valledelsol.cl");
        loginRequestMock.setPassword("12345");
    }

    @Test
    void testRegistrarUsuarioExitoso() {
        // Dado que (Given)
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("juan@valledelsol.cl");
        nuevoUsuario.setPassword("12345");

        // Cuando (When)
        Usuario resultado = usuarioService.registrarUsuario(nuevoUsuario);

        // Entonces (Then)
        assertNotNull(resultado);
        assertEquals("juan@valledelsol.cl", resultado.getEmail());
        
        // Verificamos que se llamó al repositorio para guardar
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testListarTodos() {
        // Dado que
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioMock));
        
        // Cuando
        List<Usuario> resultado = usuarioService.listarTodos();
        
        // Entonces
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testLoginExitoso() {
        // Dado que
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("12345", "passwordEncriptada")).thenReturn(true);
        when(jwtUtils.generateToken("juan@valledelsol.cl")).thenReturn("tokenFalso123");

        // Cuando
        String token = usuarioService.login(loginRequestMock);

        // Entonces
        assertNotNull(token);
        assertEquals("tokenFalso123", token);
    }

    @Test
    void testLoginFallaPorPasswordIncorrecta() {
        // Dado que
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("12345", "passwordEncriptada")).thenReturn(false); // Contraseña no coincide

        // Cuando / Entonces: Esperamos que lance una excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login(loginRequestMock);
        });

        assertEquals("Credenciales inválidas", exception.getMessage());
    }
    
    @Test
    void testFallbackLogin() {
        // Probamos que el método de emergencia del Circuit Breaker retorne el texto correcto
        String respuestaFallback = usuarioService.fallbackLogin(loginRequestMock, new RuntimeException("DB caída"));
        
        assertTrue(respuestaFallback.contains("Servicio de autenticación en modo de espera"));
        assertTrue(respuestaFallback.contains("DB caída"));
    }
}