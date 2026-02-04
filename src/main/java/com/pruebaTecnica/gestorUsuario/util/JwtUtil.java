package com.pruebaTecnica.gestorUsuario.util;

import com.pruebaTecnica.gestorUsuario.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 1. Usa una clave de al menos 32 caracteres (256 bits)
    private static final String SECRET_KEY = "EstaEsUnaClaveSuperSecretaYMuyLargaParaChakray2026";
    
    // El token durará 10 horas, se puede definir el tiempo, yo use 10 horas
    private static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;

    // Obtener la key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // GENERAR TOKEN
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER"); // Puedes agregar roles aquí si los tienes
        claims.put("name", user.getName());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getTaxId()) // Usamos taxId como identificador único
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // EXTRAER INFORMACIÓN DEL TOKEN
    public String extractTaxId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // VALIDAR SI EL TOKEN EXPIRÓ
    public Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}