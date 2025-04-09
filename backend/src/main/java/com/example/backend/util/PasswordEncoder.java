package com.example.backend.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class PasswordEncoder {

    public String encode(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Convert to hex string for storage
            StringBuilder sb = new StringBuilder();
            for (byte b : salt) {
                sb.append(String.format("%02x", b));
            }
            sb.append(":"); // Delimiter between salt and password
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            // Split salt and hashed password
            String[] parts = encodedPassword.split(":");
            String saltHex = parts[0];

            // Convert salt from hex to bytes
            byte[] salt = new byte[saltHex.length() / 2];
            for (int i = 0; i < salt.length; ++i) {
                int index = i * 2;
                salt[i] = (byte) Integer.parseInt(saltHex.substring(index, index + 2), 16);
            }

            // Hash the input password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedInputPassword = md.digest(rawPassword.getBytes());

            // Convert to hex string for comparison
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedInputPassword) {
                sb.append(String.format("%02x", b));
            }
            String hashedInputString = sb.toString();

            // Compare with stored hash
            return hashedInputString.equals(parts[1]);
        } catch (Exception e) {
            return false;
        }
    }
}
