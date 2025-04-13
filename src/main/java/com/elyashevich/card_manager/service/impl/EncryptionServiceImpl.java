package com.elyashevich.card_manager.service.impl;

import com.elyashevich.card_manager.service.EncryptionService;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1234567890abcdef";

    @Override
    public byte[] encrypt(String plainText) throws NoSuchPaddingException,
        NoSuchAlgorithmException,
        InvalidKeyException,
        IllegalBlockSizeException,
        BadPaddingException {
        var cipher = Cipher.getInstance(ALGORITHM);
        var key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plainText.getBytes());
    }

    @Override
    public String decrypt(byte[] cipherBytes) throws NoSuchPaddingException,
        NoSuchAlgorithmException,
        IllegalBlockSizeException,
        BadPaddingException,
        InvalidKeyException {
        var cipher = Cipher.getInstance(ALGORITHM);
        var key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(cipherBytes);
        return new String(result);
    }
}
