package pers.kinson.wechat.logic.system;

import javafx.scene.image.Image;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvatarCache {

    private static Map<String, Image> allAvatarCache = new HashMap<>();

    public static Image getOrCreateImage(String url) {
        if (allAvatarCache.containsKey(url)) {
            return allAvatarCache.get(url);
        }
        try {
            Image image = new Image(url);
            allAvatarCache.put(url, image);
            return image;
        } catch (Exception e) {
            List<EmojiVo> avatarList = Context.settingManager.getAllAvatar();
            Image image = new Image(avatarList.get(0).getUrl());
            allAvatarCache.put(url, image);
            System.out.println("invalid url :" + url);
            return image;
        }
    }
}
