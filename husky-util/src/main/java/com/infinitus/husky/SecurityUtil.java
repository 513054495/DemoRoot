package com.infinitus.husky;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public final class SecurityUtil {
    private static String PASSWORD = "1112222";
    private static  BASE64Encoder base64encoder = null;
    private static  BASE64Decoder base64decoder = null;
    private static final String AES = "AES";
    private static final String ENCODING = "UTF-8";
    private static final int LENGTH = 128;
    private static final String SECURE_NAME = "SHA1PRNG";
    private  static Cipher enCipher = null;
    private  static Cipher deCipher = null;
    /*    static{
        ResourceBundle resource = ResourceBundle.getBundle("config");
        PASSWORD = resource.getString("PASSWORD");
    }*/
    static {
        synchronized (SecurityUtil.class){
            try{
                base64encoder = new BASE64Encoder();
                base64decoder = new BASE64Decoder();
                KeyGenerator kgen = KeyGenerator.getInstance(AES);
                //防止linux下 随机生成key
                SecureRandom secureRandom = SecureRandom.getInstance(SECURE_NAME);
                secureRandom.setSeed(PASSWORD.getBytes());
                kgen.init(LENGTH, secureRandom);
                SecretKey secretKey = kgen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);
                enCipher = Cipher.getInstance(AES);// 创建密码器
                enCipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
                deCipher = Cipher.getInstance(AES);// 创建密码器
                deCipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            }catch (Exception e){
                e.printStackTrace();
                throw  new RuntimeException(e);
            }
        }

    }
    /**
     * AES加密
     * @param content
     * @return  String
     *
     */
    public synchronized static String encryptAES(String content) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        if (StringUtils.isBlank(content))
            return content;
        byte[] encryptResult = encrypt(content);
        String encryptResultStr = parseByte2HexStr(encryptResult);
        // BASE64位加密
        encryptResultStr = base64encoder(encryptResultStr);
        return encryptResultStr;
    }
    /**
     * AES解密
     * @param encryptResultStr
     * @return  String
     *
     */
    public synchronized static String decryptAES(String encryptResultStr) throws BadPaddingException, IllegalBlockSizeException {
        // BASE64位解密
        String decrpt = base64decoder(encryptResultStr);
        byte[] decryptFrom = parseHexStr2Byte(decrpt);
        byte[] decryptResult = decrypt(decryptFrom);
        return new String(decryptResult);
    }
    /**
     * 加密字符串(UTF-8编码)
     * @param str
     * @return  String
     */
    public synchronized static String base64encoder(String str) {

        String result = str;
        if (str != null && str.length() > 0) {
            try {
                byte[] encodeByte = str.getBytes(ENCODING);
                result = base64encoder.encode(encodeByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //base64加密超过一定长度会自动换行 需要去除换行符
        //return pattern.matcher(result).replaceAll("");
        return result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");

    }
    /**
     * 解密字符串
     * @param str
     * @return  String
     *
     */
    public synchronized static String base64decoder(String str) {
        try {
            byte[] encodeByte = base64decoder.decodeBuffer(str);
            return new String(encodeByte);
        } catch (IOException e) {
            e.printStackTrace();
            return str;
        }
    }
    /**
     * 加密
     * @param content
     * @return  byte[]
     */
    private  static byte[] encrypt(String content) throws UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {

            byte[] byteContent = content.getBytes(ENCODING);
            byte[] result = enCipher.doFinal(byteContent);
            return result; // 加密
    }
    /**
     * 解密
     * @param content
     * @return  byte[]
     *
     */
    private static byte[] decrypt(byte[] content) throws BadPaddingException, IllegalBlockSizeException {
            return deCipher.doFinal(content); // 加密
    }
    /**
     * 将二进制转换成16进制
     * @param buf
     * @return  String
     */
    public synchronized static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return  byte[]
     */
    public synchronized static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
