package com.valledelsol.ms_usuarios.ms_usuarios.service;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setEmail("test@valle.com");
        usuarioPrueba.setPassword("encoded_password");
        usuarioPrueba.setNombre("Test");
    }

    // --- TESTS DE BUSQUEDA Y LISTADO ---

    @Test
    void buscarPorEmail_Exitoso() {
        when(usuarioRepository.findByEmail("test@valle.com")).thenReturn(Optional.of(usuarioPrueba));
        Usuario resultado = usuarioService.buscarPorEmail("test@valle.com");
        assertNotNull(resultado);
        assertEquals("test@valle.com", resultado.getEmail());
    }

    @Test
    void buscarPorEmail_DebeLanzarExcepcion_CuandoNoExiste() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> usuarioService.buscarPorEmail("noexiste@test.com"));
    }

    @Test
    void listarTodos_DebeRetornarLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPrueba));
        List<Usuario> lista = usuarioService.listarTodos();
        assertFalse(lista.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
    }

    // --- TESTS DE REGISTRO Y ACTUALIZACION ---

    @Test
    void registrarUsuario_Exitoso() {

        Usuario usuarioParaRegistrar = new Usuario();
        usuarioParaRegistrar.setEmail("nuevo@test.com");
        usuarioParaRegistrar.setPassword("123456"); // Le damos una clave

        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        Usuario guardado = usuarioService.registrarUsuario(usuarioParaRegistrar);

        assertNotNull(guardado);
        verify(passwordEncoder).encode("123456"); // Verificamos que se usó la clave correcta
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizarUsuario_Exitoso() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);
        usuarioService.actualizarUsuario(usuarioPrueba);
        verify(usuarioRepository).save(usuarioPrueba);
    }

    // --- TESTS DE LOGIN ---

    @Test
    void login_Exitoso() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@valle.com");
        req.setPassword("password123");

        when(usuarioRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(anyString())).thenReturn("token_valido");

        String token = usuarioService.login(req);
        assertEquals("token_valido", token);
    }

    @Test
    void login_DebeLanzarExcepcion_CuandoPasswordIncorrecta() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@valle.com");
        req.setPassword("wrong-pass");

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> usuarioService.login(req));
    }

    @Test
    void generarToken_DebeLlamarJwtUtils() {
        when(jwtUtils.generateToken("test@valle.com")).thenReturn("token_abc");
        String token = usuarioService.generarToken("test@valle.com");
        assertEquals("token_abc", token);
        verify(jwtUtils).generateToken("test@valle.com");
    }

    @Test
    void login_CuandoUsuarioNoEncontrado_DebeLanzarExcepcion() {
        LoginRequest req = new LoginRequest();
        req.setEmail("noexiste@test.com");
        req.setPassword("123456");

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.login(req));
    }
}