package com.pruebaTecnica.gestorUsuario.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pruebaTecnica.gestorUsuario.dto.LoginRequest;
import com.pruebaTecnica.gestorUsuario.model.User;
import com.pruebaTecnica.gestorUsuario.service.UserService;
import com.pruebaTecnica.gestorUsuario.util.AESUtil;
import com.pruebaTecnica.gestorUsuario.util.JwtUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login con cifrado AES-256")

public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest req) throws Exception {
    
    User user = (User) userService.findByTaxId(req.getTaxId());

    // 1. Usuario no existe
    if (user == null) {
        return ResponseEntity.status(401).body(Map.of("message", "Usuario no encontrado"));
    }

    // 2. Validación de seguridad para la lista en memoria
    if (user.getPassword() == null) {
        return ResponseEntity.status(500).body(Map.of("message", "Error crítico: El usuario no tiene contraseña configurada."));
    }

    // 3. Desencriptar y comparar
    String passwordDecrypted = AESUtil.decrypt(user.getPassword());

    if (!passwordDecrypted.equals(req.getPassword())) {
        // Mensaje de contraseña incorrecta
        return ResponseEntity.status(401).body(Map.of("message", "Contraseña incorrecta. Inténtalo de nuevo."));
    }

    // 4. Login exitoso
    Map<String, Object> response = new HashMap<>();
    response.put("message", "¡Login exitoso! Bienvenido(a), " + user.getName());
    response.put("user", user);

    String token = jwtUtil.generateToken(user);
    response.put("token", token);
    return ResponseEntity.ok(response);
}
}