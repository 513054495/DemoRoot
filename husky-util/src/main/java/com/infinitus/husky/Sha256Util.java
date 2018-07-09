package com.infinitus.husky;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by roco on 2017/1/9.
 */
public class Sha256Util {
    //签名算法HmacSha256
    public static final String HMAC_SHA256 = "HmacSHA256";
    //编码UTF-8
    public static final String ENCODING = "UTF-8";

    /**
     *
     * sha256 加密后转base64编码,密钥是 slat
     * @param text
     * @param secret
     * @return
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static String encode(String text, String secret) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        Mac hmacSha256 = Mac.getInstance(HMAC_SHA256);
        byte[] keyBytes = secret.getBytes(ENCODING);
        hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, HMAC_SHA256));
        return new String(Base64.encodeBase64( hmacSha256.doFinal(text.getBytes(ENCODING))),ENCODING);
    }
}
