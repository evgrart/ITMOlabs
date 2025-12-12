package com.example.lab4.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // 500 мс
    }

    public static boolean check(String candidate, String hashed) {
        try {
            return BCrypt.checkpw(candidate, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}