package pers.kinson.wechat.logic.friend.message.req;

import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Getter
@Setter
public class ReqApplyFriend extends AbstractPacket {

    private Long from;

    private Long to;

    private String remark;

    @Override
    public int getPacketType() {
        return CmdConst.ReqApplyFriend;
    }
}