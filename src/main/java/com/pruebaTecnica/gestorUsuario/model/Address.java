
package com.pruebaTecnica.gestorUsuario.model;

import lombok.*;

@Data
@NoArgsConstructor 
@AllArgsConstructor

public class Address {
    private int id;
    private String name;
    private String street;
    private String countryCode;
}
