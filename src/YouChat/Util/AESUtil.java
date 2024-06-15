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
        //使用 KeyGenerator 类的 getInstance 方法获取一个 AES 密钥生成器
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        //通过 init 方法初始化密钥生成器
        //可以使用128、192、256位的密钥
        keyGen.init(256);
        //获得密钥
        return keyGen.generateKey();
    }

    //将 SecretKey 对象转换为字符串形式。
    public static String keyToString(SecretKey secretKey) {
        //它使用 Base64 编码将密钥的字节数组转换为字符串。
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    //将字符串形式的密钥转换回 SecretKey 对象
    public static SecretKey stringToKey(String keyStr) {
        //使用 Base64 解码将字符串转换为字节数组
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);

        //使用 SecretKeySpec 构造函数将字节数组转换为 SecretKey 对象
        //1.encodedKey：表示密钥的字节数组。在这里，它是经过Base64解码的原始字节数组
        //2.offset：表示从字节数组的哪个位置开始读取数据来构造密钥。在这里，offset 设为 0，表示从字节数组的第一个字节开始读取数据
        //3.len：表示从字节数组中读取的字节数，用于构造密钥。在这里，len 设置为 decodedKey.length，表示读取整个字节数组来构造密钥
        //4.algorithm：表示要使用的加密算法的名称。在这里，ALGORITHM 是一个常量，表示要使用的AES算法，即 "AES"
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    // AES加密
    public static String encrypt(String input) throws Exception {
        // 生成密钥
        SecretKey secretKey = generateKey();
        String keyStr = keyToString(secretKey);

        //使用 Cipher 类获取一个AES加密实例
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //指定加密模式为 Cipher.ENCRYPT_MODE，并传入生成的密钥
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //调用 doFinal 方法对输入的字符串进行加密，得到加密后的字节数组
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        //使用 Base64 编码将加密后的字节数组转换为字符串，并在结果字符串中添加分隔符和密钥字符串。
        return Base64.getEncoder().encodeToString(encryptedBytes) + "①②③④⑤" + keyStr;
    }

    // AES解密
    public static String decrypt(String encrypted, String secretKey) throws Exception {
        //使用 Cipher 类获取一个AES解密实例
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //指定解密模式为 Cipher.DECRYPT_MODE，并传入 SecretKey 对象
        cipher.init(Cipher.DECRYPT_MODE, stringToKey(secretKey));
        //调用 doFinal 方法对 加密后的字节数组进行解密
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decryptedBytes);
    }

}
