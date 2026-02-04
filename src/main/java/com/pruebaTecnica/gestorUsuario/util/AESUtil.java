package com.pruebaTecnica.gestorUsuario.util;


import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    //32 bytes para AES encryption
    private static final String SECRET_KEY = "12345678901234567890123456789012";

    public static String encrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(SECRET_KEY.getBytes(),"AES"));
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
    }

    public static String decrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(SECRET_KEY.getBytes(),"AES"));
        return new String(cipher.doFinal(Base64.getDecoder().decode(str)));
    }

}
