package com.alitrabelsi.passwordmanager;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * Handles data encryption using AES/GCM and Hash Derived Key Generation
 * Provides methods to encrypt and decrypt data 
 * 
 * @author Ali Trabelsi
 * @version 1.0.0
 *
 */
public class Encryption {
	private static final String ENCRYPTION_ALGO = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12; // 96 bits for GCM recommended
    private static final int TAG_LENGTH = 128; // 128-bit authentication tag
    private static final int ITERATIONS = 65536;
    private static final int SALT_SIZE = 16;

    private final String masterPassword;

    public Encryption(String masterPassword) {
        this.masterPassword = masterPassword;
    }
    
    protected String encrypt(String plainText) throws Exception{
    	byte[] salt = generateRandomBytes(SALT_SIZE);
        SecretKey key = deriveKey(masterPassword, salt);

        byte[] iv = generateRandomBytes(IV_SIZE);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Store salt:iv:ciphertext (all base64 encoded)
        return Base64.getEncoder().encodeToString(salt) + ":" +
               Base64.getEncoder().encodeToString(iv) + ":" +
               Base64.getEncoder().encodeToString(cipherText);
    }
    
    protected String decrypt(Object cipherText) throws Exception{
    	String[] parts = ((String) cipherText).split(":");
    	
        if (parts.length != 3) {
        	throw new IllegalArgumentException("ERROR : Encryption Data Missing ");
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encryptedText = Base64.getDecoder().decode(parts[2]);

        SecretKey key = deriveKey(masterPassword, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plainText = cipher.doFinal(encryptedText);
        return new String(plainText, StandardCharsets.UTF_8);
    }
    
    private SecretKey deriveKey(String password, byte[] salt) throws Exception{
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
    
    private byte[] generateRandomBytes (int size) {
    	byte[] bytes = new byte[size];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }
	
}
