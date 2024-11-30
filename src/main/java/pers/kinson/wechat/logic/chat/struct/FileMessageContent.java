package pers.kinson.wechat.logic.chat.struct;

import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.base.MessageContentType;

@Getter
@Setter
public class FileMessageContent extends MediaMessageContent {

    private String name;

    private long size;

    public FileMessageContent() {
        setType(MessageContentType.FILE);
    }

}