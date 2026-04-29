package com.valledelsol.ms_usuarios.ms_usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Clave secreta consistente para firmar y validar
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    private final int jwtExpirationMs = 86400000; // 24 horas

    // --- 1. GENERAR TOKEN (Ya lo tenías) ---
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    // --- 2. EXTRAER USERNAME/EMAIL ---
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- 3. VALIDAR TOKEN ---
    public boolean isTokenValid(String token, String email) {
        final String username = extractUsername(token);
        return (username.equals(email) && !isTokenExpired(token));
    }

    // --- MÉTODOS DE APOYO (Privados) ---

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}