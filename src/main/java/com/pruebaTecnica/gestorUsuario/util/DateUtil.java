package com.pruebaTecnica.gestorUsuario.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

//Zona horaria de Madagascar
public class DateUtil {
    public static String getMadagascarTime(){
        return LocalDateTime.now(ZoneId.of("Indian/Antananarivo"))
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
