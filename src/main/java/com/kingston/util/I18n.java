package com.kingston.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * 国际化资源池
 * @author kingston
 */
public class I18n {

    private static ResourceBundle resourcePool;

    static {
        try {
            resourcePool = ResourceBundle.getBundle("i18n/message");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key, Object... args) {
        if (!resourcePool.containsKey(key)) {
           return "国际化资源不存在";
        }
        String message = resourcePool.getString(key);
        if (args != null) {
            return MessageFormat.format(message, args);
        } else {
            return message;
        }
    }
}