package pers.kinson.wechat.logic.setting;

import javafx.scene.image.Image;
import jforgame.commons.JsonUtil;
import jforgame.commons.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.config.SystemConfig;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.util.SchedulerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SettingManager {

    List<EmojiVo> avatarList = new ArrayList<>();

    public void init() {

        SchedulerManager.INSTANCE.runDelay(() -> {
            try {
                HttpResult httpResult = Context.httpClientManager.get("/avatar/list", new HashMap<>(), HttpResult.class);
                @SuppressWarnings("all") LinkedList<EmojiVo> list = JsonUtil.string2Collection(httpResult.getData(), LinkedList.class, EmojiVo.class);
                for (EmojiVo emojiVo : list) {
                    Image image = new Image(emojiVo.getUrl());
                    emojiVo.setImage(image);
                    avatarList.add(emojiVo);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }, TimeUtil.MILLIS_PER_SECOND);
    }

    public List<EmojiVo> getAllAvatar() {
        return avatarList;
    }

}
