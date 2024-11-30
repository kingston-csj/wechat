package pers.kinson.wechat.logic.chat.struct;

import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.base.MessageContentType;

@Getter
@Setter
public class MediaMessageContent extends MessageContent {


    public MediaMessageContent() {
        setType(MessageContentType.IMAGE);
    }

    /**
     * 文件云存储路径
     */
    private String url;

}