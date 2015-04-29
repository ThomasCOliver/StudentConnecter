package app.mywork.testuizangle;

import android.provider.Settings;

import java.security.MessageDigest;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
    /**
     * Turns array of bytes into string
     *
     * @param buf Array of bytes to convert to hex string
     * @return Generated hex string
     */

    //convert hex to string hex
    private static String asHexStr(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            //if it would just be X, make it 0X
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    //get the key
    private static SecretKeySpec getSecretKeySpec(String passphrase, String algorithm, int kgenbit) throws Exception {
        // 8-byte Salt - SHOULD NOT BE DISCLOSED
        // alternative approach is to have the salt passed from the
        // calling class (pass-the-salt)?
        byte[] salt = Settings.Secure.ANDROID_ID.getBytes();

        // Iteration count
        int iterationCount = 1024;

        KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, iterationCount);

        SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(secretKey.getEncoded());
        md.update(salt);
        for (int i = 1; i < iterationCount; i++) {
            md.update(md.digest());
        }

        byte[] keyBytes = md.digest();
        SecretKeySpec skeyspec = new SecretKeySpec(keyBytes, algorithm);

        return skeyspec;
    }

    /**
     * Encrypt a byte array given the secret key spec
     *
     * @param message
     * @param passcode
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] message, String passcode) throws Exception {
        SecretKeySpec skeySpec = getSecretKeySpec(passcode, "AES", 128);
        Cipher cipher = Cipher.getInstance(skeySpec.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(message);
        return encrypted;
    }

    /**
     * Decrypt a byte array given the same secret key spec used to encrypt the message
     *
     * @param message
     * @param passcode
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] message, String passcode)throws Exception {
        SecretKeySpec skeySpec= getSecretKeySpec(passcode, "AES", 128);
        Cipher cipher = Cipher.getInstance(skeySpec.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(message);
        return decrypted;
    }
}