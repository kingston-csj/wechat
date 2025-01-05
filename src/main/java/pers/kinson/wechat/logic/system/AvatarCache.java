package pers.kinson.wechat.logic.system;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class AvatarCache {

    private static Map<String, Image> allAvatarCache = new HashMap<>();

    public static Image getOrCreateImage(String url) {
        if (allAvatarCache.containsKey(url)) {
            return allAvatarCache.get(url);
        }
        Image image = new Image(url);
        allAvatarCache.put(url, image);
        return image;
    }
}
