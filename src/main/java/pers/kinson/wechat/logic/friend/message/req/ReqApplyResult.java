package pers.kinson.wechat.logic.friend.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.net.CmdConst;

@Getter
@Setter
@MessageMeta(cmd = CmdConst.ReqApplyResult)
public class ReqApplyResult {

    private Long applyId;

    private byte status;

}
