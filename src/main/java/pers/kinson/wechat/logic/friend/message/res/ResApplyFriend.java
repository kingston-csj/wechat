package pers.kinson.wechat.logic.friend.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResApplyFriend)
public class ResApplyFriend {

    /**
     * 0表示申请成功
     */
    private int code;
}