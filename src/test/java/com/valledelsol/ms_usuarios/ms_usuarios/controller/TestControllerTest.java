package com.valledelsol.ms_usuarios.ms_usuarios.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.valledelsol.ms_usuarios.ms_usuarios.security.JwtUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestController.class)
@AutoConfigureMockMvc(addFilters = false)
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void rutaProtegida_DebeRetornarOk() throws Exception {
        mockMvc.perform(get("/api/test/protegido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("¡Acceso concedido!"))
                .andExpect(jsonPath("$.estado").value("100% Funcional"));
    }
}
