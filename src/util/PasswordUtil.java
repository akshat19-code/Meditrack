package util;

import java.security.*;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = d.digest(plainPassword.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
}