package pers.kinson.wechat.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64CodecUtil {

    public static String encode(String source) {
        byte[] body = Base64.getEncoder().encode(source.getBytes(StandardCharsets.UTF_8));
       return new String(body, StandardCharsets.UTF_8);
    }

    public static String decode(String source) {
        byte[] body = Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
        return new String(body, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String content = "这是一段中方，内容666";
        String encoded = Base64CodecUtil.encode(content);
        System.out.println(encoded);
        String decoded = Base64CodecUtil.decode(encoded);
        System.out.println(decoded);
    }
}
