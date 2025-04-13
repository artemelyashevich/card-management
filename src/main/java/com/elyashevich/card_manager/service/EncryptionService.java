package com.elyashevich.card_manager.service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface EncryptionService {

    byte[] encrypt(String plainText) throws NoSuchPaddingException,
        NoSuchAlgorithmException,
        InvalidKeyException,
        IllegalBlockSizeException,
        BadPaddingException;

    String decrypt(byte[] cipherBytes) throws NoSuchPaddingException,
        NoSuchAlgorithmException,
        IllegalBlockSizeException,
        BadPaddingException,
        InvalidKeyException;
}
