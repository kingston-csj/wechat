package pers.kinson.wechat.logic.chat.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqFetchNewMessage)
public class ReqFetchNewMessage {

    private byte channel;

    private long topic;

    private long maxSeq;

}