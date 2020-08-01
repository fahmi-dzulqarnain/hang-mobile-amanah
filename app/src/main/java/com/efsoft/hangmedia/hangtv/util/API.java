package com.efsoft.hangmedia.hangtv.util;

import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class API {
    @SerializedName("sign")
    private String signs;
    @SerializedName("salt")
    private String salts;

    public API() {
        String apiKey = "viaviweb";
        salts = "" + getRandomSalt();
        signs = md5(apiKey + salts);
    }

    private int getRandomSalt() {
        Random random = new Random();
        return random.nextInt(900);
    }

    public String getSigns() {
        return signs;
    }

    public String getSalts() {
        return salts;
    }

    private String md5(String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String toBase64(String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }
}