package pers.kinson.wechat.logic.chat.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResNewMessageNotify)
public class ResNewMessageNotify {

    private byte channel;

    private String topic;
}
