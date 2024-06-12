package YouChat.Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";

    // 生成AES密钥
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // 可以使用128、192、256位的密钥
        return keyGen.generateKey();
    }

    // 将密钥转换为字符串
    public static String keyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // 将字符串转换为密钥
    public static SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    // AES加密
    public static String encrypt(String input) throws Exception {
        // 生成密钥
        SecretKey secretKey = generateKey();
        String keyStr = keyToString(secretKey);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes) + "①②③④⑤" + keyStr;
    }

    // AES解密
    public static String decrypt(String encrypted, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, stringToKey(secretKey));
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decryptedBytes);
    }

}
