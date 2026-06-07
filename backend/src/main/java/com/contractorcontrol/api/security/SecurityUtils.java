package com.contractorcontrol.api.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.SCrypt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  private static final int SALT_LENGTH = 16;
  private static final int HASH_LENGTH = 64;
  private static final int COST = 16384;
  private static final int BLOCK_SIZE = 8;
  private static final int PARALLELIZATION = 1;
  private final SecureRandom secureRandom = new SecureRandom();

  public String hashPassword(String password) {
    byte[] salt = new byte[SALT_LENGTH];
    secureRandom.nextBytes(salt);
    byte[] hash = SCrypt.generate(password.getBytes(StandardCharsets.UTF_8), salt, COST, BLOCK_SIZE, PARALLELIZATION, HASH_LENGTH);
    return toHex(salt) + ":" + toHex(hash);
  }

  public boolean verifyPassword(String password, String storedHash) {
    if (storedHash == null) {
      return false;
    }

    String[] parts = storedHash.split(":", 2);
    if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
      return false;
    }

    byte[] salt = fromHex(parts[0]);
    byte[] expected = fromHex(parts[1]);
    byte[] actual = SCrypt.generate(password.getBytes(StandardCharsets.UTF_8), salt, COST, BLOCK_SIZE, PARALLELIZATION, HASH_LENGTH);

    if (actual.length != expected.length) {
      return false;
    }

    int diff = 0;
    for (int i = 0; i < actual.length; i++) {
      diff |= actual[i] ^ expected[i];
    }
    return diff == 0;
  }

  private String toHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte value : bytes) {
      builder.append(String.format("%02x", value));
    }
    return builder.toString();
  }

  private byte[] fromHex(String value) {
    int length = value.length();
    byte[] data = new byte[length / 2];
    for (int i = 0; i < length; i += 2) {
      data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4) + Character.digit(value.charAt(i + 1), 16));
    }
    return data;
  }
}
