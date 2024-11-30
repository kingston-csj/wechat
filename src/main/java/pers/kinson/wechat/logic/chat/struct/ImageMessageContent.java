package pers.kinson.wechat.logic.chat.struct;

import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.base.MessageContentType;

@Getter
@Setter
public class ImageMessageContent extends MediaMessageContent {


    public ImageMessageContent() {
        setType(MessageContentType.IMAGE);
    }

}