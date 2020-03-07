package org.desperu.openluks.utils;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import static android.content.ContentValues.TAG;

public class EncryptedFile {

    private static byte[] iv = new byte[16];
    private static byte[] salt = new byte[32];
    private static byte[] ctChunk = new byte[8192]; // not for whole ciphertext, just a buffer
    //TODO on test use keystore
    private static byte[] saveKey = null;

    public static void saveDataToEncryptedFile(File file, String submittedPassword) {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream ctBaos = new ByteArrayOutputStream();

        try {
            fileInputStream = new FileInputStream(file);
            if (16 != fileInputStream.read(iv) || 32 != fileInputStream.read(salt)) {
                throw new Exception("IV or salt too short");
            }

            KeySpec keySpec = new PBEKeySpec(submittedPassword.toCharArray(), salt, 1000, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);

            int read;
            while ((read = fileInputStream.read(ctChunk)) > 0) {
//                ctBaos.write(cipher.update(cipherText, 0, read));
            }
            ctBaos.write(cipher.doFinal());

            String plainStr = new String(ctBaos.toByteArray(), "UTF-8");
//            textEdit.setText(plainStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------
    // GENERATE KEY
    // --------------

//    public static byte[] generateKey(@NotNull String password) throws Exception {
//        byte[] keyStart = password.getBytes(StandardCharsets.UTF_8);
//
//        KeyGenerator kGen = KeyGenerator.getInstance("AES");
//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//        sr.setSeed(keyStart);
//        kGen.init(256, sr);
//        SecretKey sKey = kGen.generateKey();
//        return sKey.getEncoded();
//    }

    private static byte[] generateKey(@NotNull String password) throws Exception {
        /* Store these things on disk used to derive key later: */
        int iterationCount = 1000;
        int saltLength = 32; // bytes; should be the same size as the output (256 / 8 = 32)
        int keyLength = 256; // 256-bits for AES-256, 128-bits for AES-128, etc
//        byte[] salt; // Should be of saltLength

        /* When first creating the key, obtain a salt with this: */
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        /* Use this to derive the key from the password: */
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
//        return keyFactory.generateSecret(keySpec).getEncoded();
        // TODO for test Key Store
        showAndroidKeyStored();
        SecretKey sKey = keyFactory.generateSecret(keySpec);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            saveKeyToKeyStore(sKey, password);
        }
        return sKey.getEncoded();
    }

    // --------------
    // MANAGE KEYSTORE
    // --------------

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void saveKeyToKeyStore(SecretKey mySecretKey, @NotNull String password) {
        KeyStore ks;

        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
//            FileInputStream fis = new FileInputStream("keyStoreName");
//            ks.load(fis, password.toCharArray());

            KeyStore.ProtectionParameter protParam =
                    new KeyStore.PasswordProtection(password.toCharArray());

            KeyProtection keyProtection = new KeyProtection.Builder(KeyProperties.PURPOSE_WRAP_KEY).build();

            // save my secret key
            KeyStore.SecretKeyEntry skEntry =
                    new KeyStore.SecretKeyEntry(mySecretKey);
            ks.setEntry("secretKeyAlias", skEntry, keyProtection);

//            KeyStore.Entry entry = ks.setKeyEntry("secretKeyAlias", mySecretKey, password.toCharArray(), );
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static byte[] getKeyFromKeyStore(@NotNull String password) {
        KeyStore ks;
        SecretKey sKey = null;

        try {
            ks = KeyStore.getInstance("AndroidKeyStore");

//            FileInputStream fis = new FileInputStream("keyStoreName");
//            ks.load(fis, password.toCharArray());

            ks.load(null);

            KeyStore.ProtectionParameter protParam =
                    new KeyStore.PasswordProtection(password.toCharArray());

            // get my private key
//            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
//                    ks.getEntry("privateKeyAlias", protParam);
//            PrivateKey myPrivateKey = pkEntry.getPrivateKey();

            KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry)
                    ks.getEntry("secretKeyAlias", protParam);
            sKey = skEntry != null ? skEntry.getSecretKey() : null;
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }

        return sKey != null ? sKey.getEncoded() : new byte[32];
    }

    private static void showAndroidKeyStored() {
        /*
         * Load the Android KeyStore instance using the
         * "AndroidKeyStore" provider to list out what entries are
         * currently stored.
         */
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            List<String> keyAliases = new ArrayList<>();
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
            Log.d("EncryptFile AndroidKey", "Android Key Store aliases" + keyAliases.toString());
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // --------------
    // ENCODE DECODE FILE
    // --------------

    private static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);

        return cipher.doFinal(fileData);
    }

    private static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);

        return cipher.doFinal(fileData);
    }

    // --------------
    // ACCESS ENCODED FILE
    // --------------

    public static void setEncodedFile(File file, String password, String data) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            saveKey = generateKey(password);
            fos.write(encodeFile(saveKey, data.getBytes(StandardCharsets.UTF_8)));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDecodedFile(File file, String password) {
        String result = "";

        byte[] key = getKeyFromKeyStore(password);

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] fileContent = new byte[(int) file.length()];
            fis.read(fileContent);
            result = new String(decodeFile(key,fileContent));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}