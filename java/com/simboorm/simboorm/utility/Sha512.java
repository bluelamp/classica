package com.simboorm.simboorm.utility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha512 {
    public static String hash(String input) {
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(input.getBytes(StandardCharsets.UTF_8));

            return String.format("%0128x", new BigInteger(1, messageDigest.digest()));
        } catch(NoSuchAlgorithmException noSuchAlgorithmException) {
            return null;
        }
    }
}
