package geoguard.geoguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Dan on 10/25/2015.
 */
public class encryptDecrypt {


    public static void encryptDecryptFile(String inputFilename, String outputFilename, boolean encrypt , byte[] key, Context keyContext){
        FileInputStream inputStream;
        FileOutputStream outputStream;
        //if(encrypt) {
        //    buffSize = 15;
       // }else{
        //    buffSize = 16;
        // }
        try{
            inputStream = keyContext.openFileInput(inputFilename);
            outputStream = keyContext.openFileOutput(outputFilename, Context.MODE_PRIVATE);
            int buffSize = (int)inputStream.getChannel().size();
            byte[] buff = new byte[buffSize];
            int reader = inputStream.read(buff);
            while(reader != -1){
                if(encrypt){
                     buff = encryptBytes(key, keyContext, buff);
                }else{
                    buff = decryptBytes(key, keyContext, buff);
                }
                outputStream.write(buff);
                //if(encrypt){
                //    buff= new byte[15];
                //}else{
                //    buff = new byte[16];
               //}

                reader = inputStream.read(buff);
            }

            outputStream.close();
            inputStream.close();
        }catch (Exception e){
            //probably b/c not a valid file
            e.printStackTrace();
        }

    }

    public static void encryptDecryptFile(String filename, boolean encrypt , byte[] key, Context keyContext){
        FileInputStream inputStream;
        FileOutputStream outputStream;
        try{
            inputStream = keyContext.openFileInput(filename);
            int buffSize = (int)inputStream.getChannel().size();
            byte[] buff = new byte[buffSize];
            int reader = inputStream.read(buff);
            while(reader != -1){
                if(encrypt){
                    buff = encryptBytes(key, keyContext, buff);
                }else{
                    buff = decryptBytes(key, keyContext, buff);
                }
                reader = inputStream.read(buff);
            }
            inputStream.close();
            outputStream = keyContext.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(buff);
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static byte[] masterKeyGenerate(byte[] password, Context ctx) {
        byte[] salt = new byte[16];
        try {
            FileInputStream inputStream = ctx.openFileInput("saltFile");
            int buffSize = (int) inputStream.getChannel().size();
            salt = new byte[buffSize];
            int reader = 0;
            while (reader != -1) {
                reader = inputStream.read(salt);
            }
            inputStream.close();
        }catch (FileNotFoundException e){
            salt=saltGenerate(ctx);
        }catch (Exception e){

        }
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] master = new byte[password.length + salt.length];
            System.arraycopy(salt, 0, master, 0, salt.length);
            System.arraycopy(password, 0, master, salt.length, password.length);
            for(int i=0; i< 1024; i++) {
                master = digest.digest(master);
            }
            master = Arrays.copyOf(master, 16); // use only first 128 bit
            return master;

        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[16];
    }

    public static byte[] saltGenerate(Context ctx){
        SecureRandom rando;
        try {
            rando = SecureRandom.getInstance("SHA1PRNG");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        rando.setSeed(System.currentTimeMillis());
        byte[] returnval = rando.getSeed(16);

        try{
            FileOutputStream outputStream = ctx.openFileOutput("saltFile", ctx.MODE_PRIVATE);
            outputStream.write(returnval);
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return returnval;
    }

    public static byte[] encryptBytes(byte[] key, Context ctx, byte[] toencypt) throws Exception{
        byte[] iv = new byte[16];
        try{
            FileInputStream inputStream = ctx.openFileInput("saltFile");
            int buffSize = (int) inputStream.getChannel().size();
            iv = new byte[buffSize];
            int reader = 0;
            while (reader != -1) {
                reader = inputStream.read(iv);
            }
            inputStream.close();

        }catch(FileNotFoundException e){

        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));

        byte[] encrypted = cipher.doFinal(toencypt);

        return encrypted;
    }

    public static byte[] decryptBytes(byte[] key, Context ctx, byte[] todecrypt) throws Exception{
        byte[] iv = new byte[16];
        try{
            FileInputStream inputStream = ctx.openFileInput("saltFile");
            int buffSize = (int) inputStream.getChannel().size();
            iv = new byte[buffSize];
            int reader = 0;
            while (reader != -1) {
                reader = inputStream.read(iv);
            }
            inputStream.close();
        }catch(FileNotFoundException e){

        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec spec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, spec);

        byte[] decrypted = cipher.doFinal(todecrypt);

        return decrypted;
    }




}
