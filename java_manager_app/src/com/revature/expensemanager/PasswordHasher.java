package com.revature.expensemanager;

import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
    private PasswordHasher() {
    }

    public static boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split("\\$", 4);
        if (parts.length != 4 || !"pbkdf2_sha256".equals(parts[0])) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = HexFormat.of().parseHex(parts[2]);
            byte[] expectedDigest = HexFormat.of().parseHex(parts[3]);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, expectedDigest.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] actualDigest = factory.generateSecret(spec).getEncoded();
            return java.security.MessageDigest.isEqual(actualDigest, expectedDigest);
        } catch (GeneralSecurityException | IllegalArgumentException error) {
            return false;
        }
    }
}
