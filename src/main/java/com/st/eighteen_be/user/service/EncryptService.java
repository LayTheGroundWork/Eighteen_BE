package com.st.eighteen_be.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class EncryptService {

    private final AesBytesEncryptor encryptor;

    public EncryptService(@Value("${symmetric.key}") String password,
                          @Value("${symmetric.salt}") String salt) {
        this.encryptor = new AesBytesEncryptor(password, salt);
    }

    // 암호화
    public String encryptPhoneNumber(String phoneNumber) {
        byte[] encrypt = encryptor.encrypt(phoneNumber.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    // 복호화
    public String decryptPhoneNumber(String encryptString) {
        byte[] decryptBytes = stringToByteArray(encryptString);
        byte[] decrypt = encryptor.decrypt(decryptBytes);

        return new String(decrypt, StandardCharsets.UTF_8);
    }

    // byte -> String
    public String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte byt : bytes){
            sb.append(byt);
            sb.append(" ");
        }
        return sb.toString();
    }

    // String -> byte
    public byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer byteBuffer = ByteBuffer.allocate(split.length);
        for (String s : split) {
            byteBuffer.put((byte) Integer.parseInt(s));
        }
        return byteBuffer.array();
    }
}
