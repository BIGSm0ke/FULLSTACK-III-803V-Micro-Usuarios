package com.valledelsol.ms_usuarios.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valledelsol.ms_usuarios.ms_usuarios.dto.LoginRequest;
import com.valledelsol.ms_usuarios.ms_usuarios.model.Usuario;
import com.valledelsol.ms_usuarios.ms_usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

// Estos son IMPORTS ESTÁTICOS (muy importantes para que las funciones de test funcionen)
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Esto apaga la seguridad para que no pida tokens
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    // AÑADE ESTOS DOS MOCKS AQUÍ ABAJO:
    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockBean
    private com.valledelsol.ms_usuarios.ms_usuarios.security.JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = Usuario.builder()
                .id(1L)
                .email("test@valle.com")
                .nombre("Test")
                .apellido("User")
                .rol("USER")
                .build();
    }

    @Test
    void registrar_DebeRetornar200yToken() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "test@valle.com");
        body.put("password", "123456");
        body.put("name", "Test");

        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(usuarioPrueba);
        when(usuarioService.generarToken("test@valle.com")).thenReturn("token-fake");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-fake"))
                .andExpect(jsonPath("$.user.email").value("test@valle.com"));
    }

    @Test
    void login_Exitoso_DebeRetornarToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@valle.com");
        request.setPassword("123456");

        when(usuarioService.login(any(LoginRequest.class))).thenReturn("token-login");
        when(usuarioService.buscarPorEmail("test@valle.com")).thenReturn(usuarioPrueba);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-login"));
    }

    @Test
    void login_Fallido_DebeRetornar401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("error@test.com");

        when(usuarioService.login(any())).thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    void getProfile_ConHeader_DebeRetornarUsuario() throws Exception {
        when(usuarioService.buscarPorEmail("test@valle.com")).thenReturn(usuarioPrueba);

        mockMvc.perform(get("/users/profile")
                .header("X-User-Name", "test@valle.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@valle.com"));
    }

    @Test
void updateProfile_DebeActualizaryRetornar200() throws Exception {
    Map<String, String> updates = new HashMap<>();
    updates.put("nombre", "NuevoNombre");

    // Cambiamos el nombre al objeto que devolverá el mock para que coincida con la expectativa
    usuarioPrueba.setNombre("NuevoNombre"); 
    when(usuarioService.buscarPorEmail("test@valle.com")).thenReturn(usuarioPrueba);

    mockMvc.perform(put("/users/profile")
            .header("X-User-Name", "test@valle.com")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("NuevoNombre"));
}

    @Test
    void health_DebeRetornarEstadoUp() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("UP"));
    }
    
}