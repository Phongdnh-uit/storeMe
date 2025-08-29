package com.DPhong.storeMe.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class BackupCodeGenerator {
  private static final String CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final SecureRandom random = new SecureRandom();

  public static List<String> generateBackupCode(int numberOfCode, int codeLength) {
    List<String> backupCodes = new ArrayList<>();
    for (int i = 0; i < numberOfCode; i++) {
      StringBuilder code = new StringBuilder(codeLength);
      for (int j = 0; j < codeLength; j++) {
        int index = random.nextInt(CHARACTERS.length());
        code.append(CHARACTERS.charAt(index));
      }
      backupCodes.add(code.toString());
    }
    return backupCodes;
  }
}
