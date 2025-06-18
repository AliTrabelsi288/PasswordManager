package com.alitrabelsi.passwordmanager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Handles password hashing using PBKDF2 with HMAC SHA-256.
 * Provides methods to hash and verify passwords securely.
 * 
 * @author Ali Trabelsi
 * @version 1.0.0
 */
public class Hash {
    private final int iterations;
    private final int keyLength;

    public Hash(int iterations, int keyLength) {
        this.iterations = iterations;
        this.keyLength = keyLength;
    }
    
    protected Boolean comparePasswords(String storedPassword, String providedPassword) throws Exception {
        String[] split = storedPassword.split(":");
        String base64salt = split[0];
        String storedHash = split[1];
        String regeneratedHash = hashPassword(providedPassword, base64salt);
        
        return regeneratedHash.equals(storedHash);
    }

    protected String hashPassword(String password, String saltBase64) throws Exception {
        byte[] salt = Base64.getDecoder().decode(saltBase64);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    protected String hashAndSaltPassword(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHash = Base64.getEncoder().encodeToString(hash);

        return encodedSalt + ":" + encodedHash;
    }
} 