package com.GSTCP.ms_security.Services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;


@Service
public class EncryptionService {
    // devuelve un String de la cual recibe la contraseña y lo va a convertir a
    // SHA256 y lo va a devolver
    public String convertSHA256(String password) {
        // creo un objeto de tipo MessageDigest y hace las posibles combinaciones y
        // permutaciones
        MessageDigest md = null;
        try {
            // entra el algoritomo de SHA-256 a la intancia de MessageDigest
            md = MessageDigest.getInstance("SHA-256");
        }
        // si no encuentra el algoritmo retorne null
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        // DESPUES GENERA EL HASH LO QUE HACE ES QUE TOME LA CONTRASEÑA QUE LE ENVIE Y
        // OBTENGA EL HASH Y
        // EMPIECE HACER EL PROCESO DE COMVERSION DE LA CONTRASEÑA A SHA-256
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Metodo para generar un codigo aleatorio
    public String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(length);
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

}