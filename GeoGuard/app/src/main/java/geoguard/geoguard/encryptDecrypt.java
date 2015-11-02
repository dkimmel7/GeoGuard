package geoguard.geoguard;

import android.content.Context;
import android.content.SharedPreferences;



import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Dan on 10/25/2015.
 */
public class encryptDecrypt {

    public static byte[] masterKeyGenerate(byte[] password, Context ctx) throws Exception{
        SharedPreferences saltPref = ctx.getSharedPreferences("salt", ctx.MODE_PRIVATE);

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] salt = saltPref.getString("salt", "").getBytes("UTF-8");
            if(salt.equals("")) {
                salt=saltGenerate();
                SharedPreferences.Editor edit = saltPref.edit();
                edit.putString("salt", new String(salt,"UTF-8"));
                edit.commit();
            }
            byte[] master = new byte[password.length + salt.length];
            System.arraycopy(salt, 0, master, 0, salt.length);
            System.arraycopy(password, 0, master, salt.length, password.length);
            for(int i=0; i< 1024; i++) {
                master = digest.digest(master);
            }
            return master;

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static byte[] saltGenerate(){
        SecureRandom rando;
        try {
            rando = SecureRandom.getInstance("SHA1PRNG");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        rando.setSeed(System.currentTimeMillis());
        return rando.getSeed(16);
    }

    public static byte[] encryptFile(byte[] key, Context ctx, byte[] toencypt) throws Exception{
        String iv = ctx.getSharedPreferences("salt", ctx.MODE_PRIVATE).getString("salt","");
        if(iv.equals("")){
            //todo handle error
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv.getBytes("UTF-8"),0,16));

        byte[] encrypted = cipher.doFinal(toencypt);

        return encrypted;
    }

    public static byte[] decryptFile(byte[] key, Context ctx, byte[] todecrypt) throws Exception{
        String iv = ctx.getSharedPreferences("salt", ctx.MODE_PRIVATE).getString("salt","");
        if(iv.equals("")){
            //todo handle error
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv.getBytes("UTF-8"),0,16));

        byte[] decrypted = cipher.doFinal(todecrypt);

        return decrypted;
    }




}
