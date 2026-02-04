package com.pruebaTecnica.gestorUsuario.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String taxId;
    private String password;
}
