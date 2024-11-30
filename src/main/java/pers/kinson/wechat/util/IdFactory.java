package pers.kinson.wechat.util;

import java.util.UUID;

public class IdFactory {

    public static String nextUUId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
