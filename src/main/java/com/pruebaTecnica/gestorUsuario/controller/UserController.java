package com.pruebaTecnica.gestorUsuario.controller;

import com.pruebaTecnica.gestorUsuario.model.User;
import com.pruebaTecnica.gestorUsuario.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")

public class UserController {

private final UserService userService;

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String sortedBy,
                               @RequestParam(required = false) String filter) {
        if (filter != null) return userService.filterUsers(filter);
        return userService.getUsersSorted(sortedBy);
    }


    @PostMapping
    public User createUser(@RequestBody User user) throws Exception {
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
        public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {
        
        try {
            // Validar que el Map no esté vacío
            if (updates == null || updates.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No se enviaron datos para actualizar"));
            }
            
            // Llamar al servicio
            User updatedUser = userService.updateUser(id, updates);
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado");
    }
}
