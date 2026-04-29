package com.valledelsol.ms_usuarios.ms_usuarios.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protegido")
    public ResponseEntity<?> rutaProtegida() {
        return ResponseEntity.ok(Map.of(
            "mensaje", "¡Acceso concedido!",
            "detalle", "Si puedes leer esto, tu filtro JWT está validando correctamente a los usuarios de Valle del Sol.",
            "estado", "100% Funcional"
        ));
    }
}