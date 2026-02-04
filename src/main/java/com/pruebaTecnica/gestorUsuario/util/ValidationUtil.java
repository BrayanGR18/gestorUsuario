package com.pruebaTecnica.gestorUsuario.util;

public class ValidationUtil {
  private static final String RFC_REGEX="^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$";
    private static final String PHONE_REGEX="^(\\+\\d{1,3})?\\d{10}$";

    public static boolean isValidRFC(String rfc){ return rfc!=null && rfc.matches(RFC_REGEX);}
    public static boolean isValidPhone(String phone){ return phone!=null && phone.matches(PHONE_REGEX);}
}

