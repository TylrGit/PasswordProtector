package org.tyler.passprotector.passprotector;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class AES {
    // Base64.getEncoder().encodeToString( byte array );
    // Base64.getDecoder().decode( "" );
    // For converting in between byte arrays and strings

    // Create Salt / Create Initialization Vector (Acts like a salt)
    public static String generateSaltOrIV() {
        byte[] byteArr = new byte[16]; // 16 bytes(128 bit) salt size
        new SecureRandom().nextBytes(byteArr);
        return Base64.getEncoder().encodeToString(byteArr);
    }

    // Plain key to AES key using salt
    private static SecretKey getKeyFromPassword(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    // Encrypt plaintext
    public static String encrypt(String plaintext, String password, String salt, String ivEncoded) throws Exception {
        SecretKey key = getKeyFromPassword(password, salt);
        IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(ivEncoded));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        return Base64.getEncoder().encodeToString(ciphertext); // Encrypted String
    }

    // Decrypt ciphertext
    public static String decrypt(String encryptedText, String password, String salt, String ivEncoded) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(encryptedText);

        SecretKey key = getKeyFromPassword(password, salt);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Base64.getDecoder().decode(ivEncoded)));

        byte[] plaintextBytes = cipher.doFinal(ciphertext);
        return new String(plaintextBytes); // Decrypted String
    }
}

