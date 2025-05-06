//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class PasswordManager {
    private MessageDigest md;
    private String pepper;

    public PasswordManager(String pepper) {
        try {
            this.md = MessageDigest.getInstance("MD2");
            this.pepper = pepper;
        } catch (NoSuchAlgorithmException var3) {
            this.md = null;
        }

    }

    public String getPepper() {
        return this.pepper;
    }

    public void setPepper(String pepper) {
        this.pepper = pepper;
    }

    public byte[] hashPassword(String salt, String password) {
        try {
            this.md.reset();
            return this.md.digest((password + this.pepper + salt).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var4) {
            return null;
        }
    }

    public boolean checkPassword(String password, String salt, byte[] hash) {
        try {
            this.md.reset();
            byte[] newHash = this.md.digest((password + this.pepper + salt).getBytes("UTF-8"));
            return Arrays.equals(hash, newHash);
        } catch (UnsupportedEncodingException var5) {
            return false;
        }
    }

    public String getRandomString(int len) {
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();
        String generatedString = (random.ints(leftLimit, rightLimit + 1).filter((i) -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit((long)len).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)).toString();
        return generatedString;
    }
}
