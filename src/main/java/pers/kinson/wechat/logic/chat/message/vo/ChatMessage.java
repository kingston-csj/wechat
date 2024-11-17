package pers.kinson.wechat.logic.chat.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.net.CmdConst;

@MessageMeta(cmd = CmdConst.ChatMessageVo)
@Data
public class ChatMessage {

    private long seq;

    private Long userId;

    private String userName;


    private byte type;
    private String json;

    private String date;

    private transient MessageContent content;

}
