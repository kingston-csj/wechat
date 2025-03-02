package pers.kinson.wechat.logic.system;

import javafx.scene.image.Image;
import jforgame.commons.JsonUtil;
import jforgame.commons.TimeUtil;
import lombok.Getter;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.database.SqliteDbUtil;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.chat.struct.Resource;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.ui.controller.ProgressMonitor;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EmojiCache {

    @Getter
    private static ConcurrentMap<String, EmojiVo> emojiVoMap = new ConcurrentHashMap<>();

    public static void  loadEmoji() {
        SchedulerManager.INSTANCE.runDelay(() -> {
            try {
                HttpResult httpResult = Context.httpClientManager.get("/emoji/list", new HashMap<>(), HttpResult.class);
                @SuppressWarnings("all") LinkedList<EmojiVo> list = JsonUtil.string2Collection(httpResult.getData(), LinkedList.class, EmojiVo.class);
                Map<String, Resource> localFaces = SqliteDbUtil.queryEmoijResource().stream().collect(Collectors.toMap(Resource::getLabel, Function.identity()));
                for (EmojiVo emojiVo : list) {
                    Image image;
                    Resource localRes = localFaces.get(emojiVo.getLabel());
                    if (localRes != null) {
                        String url = "asserts/emoji/" + localRes.getUrl();
                        image = new Image(Files.newInputStream(new File(url).toPath()));
                    } else {
                        image = new Image(emojiVo.getUrl());
                        String imageName = emojiVo.getUrl().substring(emojiVo.getUrl().lastIndexOf("/") + 1);
                        Context.httpClientManager.downloadFile(emojiVo.getUrl(), "asserts/emoji/" + imageName, new ProgressMonitor());
                        SqliteDbUtil.insertFace(emojiVo.getLabel(), imageName);
                    }
                    emojiVo.setImage(image);
                    emojiVoMap.put(emojiVo.getLabel(), emojiVo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, TimeUtil.MILLIS_PER_SECOND);
    }
}
