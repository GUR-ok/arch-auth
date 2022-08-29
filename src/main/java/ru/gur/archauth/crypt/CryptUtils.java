package ru.gur.archauth.crypt;

public class CryptUtils {

    private static final UpdatableBCrypt bcrypt = new UpdatableBCrypt(11);

    public static String hash(String password) {
        return bcrypt.hash(password);
    }

    public static boolean verifyAndUpdateHash(String password, String hash) {
        return bcrypt.verifyHash(password, hash);
    }
}
