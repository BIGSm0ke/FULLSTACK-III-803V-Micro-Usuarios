package com.valledelsol.ms_usuarios.ms_usuarios.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Generamos una clave segura para firmar el token
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // El token durará 24 horas (86400000 ms)
    private final int jwtExpirationMs = 86400000;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }
}