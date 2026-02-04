package com.pruebaTecnica.gestorUsuario.service;


import java.lang.reflect.Field;
import java.util.*;
import com.pruebaTecnica.gestorUsuario.model.*;
import com.pruebaTecnica.gestorUsuario.util.*;

import org.springframework.util.ReflectionUtils;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;


@Service
public class UserService {

    private final List<User> users = new ArrayList<>();

@PostConstruct
public void init() throws Exception {
        users.add(createSample("user1@gmail.com","user1","2212345678", "AARR990101XXX"));
        users.add(createSample("user2@mail.com","user2","5522345679","BBBB990101XXX"));
        users.add(createSample("user3@hotmail.com","user3","5552345680","CCCC990101XXX"));
    }

private User createSample(String email, String name, String phone, String taxId) throws Exception {
    return User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .name(name)
            .phone(phone)
            .password(AESUtil.encrypt("123456")) // Se asigna explícitamente al campo password
            .taxId(taxId)
            .createdAt(DateUtil.getMadagascarTime())
            .addresses(List.of(
                    new Address(1, "workaddress", "street No.1", "UK"),
                    new Address(2, "homeaddress", "street No.2", "AU")
            ))
            .build();
}



    public List<User> getUsersSorted(String field) {
        if (field == null) return users;

        return users.stream().sorted((u1,u2)-> switch (field) {
            case "email" -> u1.getEmail().compareTo(u2.getEmail());
            case "name" -> u1.getName().compareTo(u2.getName());
            case "phone" -> u1.getPhone().compareTo(u2.getPhone());
            case "tax_id" -> u1.getTaxId().compareTo(u2.getTaxId());
            default -> 0;
        }).toList();
    }

    public List<User> filterUsers(String filter) {

       String separator = filter.contains("|") ? "\\|" : "\\+";
        String[] parts = filter.split(separator);

        if (parts.length < 3) {
        return users;
    }

        String field = parts[0].trim(), op = parts[1].trim(), value = parts[2].trim();



        return users.stream().filter(u -> {
            String fieldValue = switch (field) {
                case "id" -> u.getId().toString();
                case "name" -> u.getName();
                case "email" -> u.getEmail();
                case "phone" -> u.getPhone();
                case "taxId" -> u.getTaxId();
                case "createdAt" -> u.getCreatedAt();
                default -> "";
            };

            
        String compareValue = fieldValue.toLowerCase();

        return switch (op) {
            case "co" -> compareValue.contains(value);
            case "eq" -> compareValue.equals(value);
            case "sw" -> compareValue.startsWith(value);
            case "ew" -> compareValue.endsWith(value);
            default -> false;
            };
        }).toList();
    }

    public User createUser(User user) throws Exception {
        if (!ValidationUtil.isValidRFC(user.getTaxId())) throw new RuntimeException("Invalid RFC");
        if (!ValidationUtil.isValidPhone(user.getPhone())) throw new RuntimeException("Invalid phone");
        if (users.stream().anyMatch(u -> u.getTaxId().equals(user.getTaxId())))
            throw new RuntimeException("El RFC es unico");

        user.setId(UUID.randomUUID());
        user.setPassword(AESUtil.encrypt(user.getPassword()));
        user.setCreatedAt(DateUtil.getMadagascarTime());

        users.add(user);
        return user;
    }
   public User updateUser(UUID id, Map<String, Object> updates) {
    // 1. Buscar el usuario
    User user = users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

    // 2. Aplicar cambios dinámicamente
    updates.forEach((key, value) -> {
        // Buscamos el campo en la clase User
        Field field = ReflectionUtils.findField(User.class, key);

        if (field != null) {
            field.setAccessible(true); 
            
            // Lógica especial para validaciones (ej. el teléfono)
            if (key.equals("phone") && !ValidationUtil.isValidPhone(value.toString())) {
                throw new RuntimeException("Formato invalida para phone");
            }
             if (key.equals("taxId") && !ValidationUtil.isValidRFC(value.toString())) {
                throw new RuntimeException("Formato invalido para RFC");
            }

            ReflectionUtils.setField(field, user, value);
        } else {
            System.out.println("El campo " + key + " no existe en la entidad User");
        }
    });

    return user;
}

    public void deleteUser(UUID id){ users.removeIf(u->u.getId().equals(id)); }

    public User findByTaxId(String taxId){
        return users.stream().filter(u->u.getTaxId().equals(taxId)).findFirst().orElse(null);
    }

}
