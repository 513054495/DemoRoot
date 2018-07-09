package com.infinitus.husky;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;


public class EncryptionUtil {
    public static StandardPBEStringEncryptor ENCRYPTOR = new StandardPBEStringEncryptor();

      static {
          EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
          config.setAlgorithm("PBEWithMD5AndDES");
          config.setPassword("1BDF5159811E4B4F805C191BA75A4BAC");     //rocoinfo_infinitus_oa
          ENCRYPTOR.setConfig(config);
      }

    public static String encrypt(String message) {
        return HexUtil.encode(ENCRYPTOR.encrypt(message));
    }

    public static String decrypt(String encryptedMessage) {
        return ENCRYPTOR.decrypt(HexUtil.decode(encryptedMessage));
    }

    public static void main(String[] args) {
        String usrname="5169626E6536446E395247774B6A6B364F50754D576135485653646B44587273";
        String password="4B6E4E635036786A6961715971792B2F4F5A6D4139413D3D";
        System.out.println(decrypt(usrname));
        System.out.println(decrypt(password));
        System.out.println(encrypt("root"));
        System.out.println(encrypt("gkl1234"));
    }
}
