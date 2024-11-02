package pers.kinson.wechat.logic.friend.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.net.CmdConst;

@Getter
@Setter
@MessageMeta(cmd = CmdConst.ResApplyResult)
public class ResApplyResult {

    private Long applyId;

    private byte status;

    private Long targetId;

    private String name;
}
