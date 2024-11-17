package pers.kinson.wechat.logic.chat.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.chat.struct.MessageContent;
import pers.kinson.wechat.net.CmdConst;

@MessageMeta(cmd = CmdConst.ChatMessageVo)
@Data
public class ChatMessage {

    private long id;

    private long senderId;
    private String senderName;

    private long receiverId;
    private String receiverName;

    private byte type;
    private String json;

    private String date;

    private transient MessageContent content;

}
