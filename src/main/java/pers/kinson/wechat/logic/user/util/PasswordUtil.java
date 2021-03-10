package pers.kinson.wechat.logic.user.util;

import org.apache.commons.codec.digest.Md5Crypt;

public class PasswordUtil {

    /**
     * 密码明文+盐（帐号id），求md5
     * @param userId
     * @param password
     * @return
     */
    public static String passwordEncryption(long userId, String password) {
        return Md5Crypt.apr1Crypt(password.getBytes(), String.valueOf(userId));
    }
}
