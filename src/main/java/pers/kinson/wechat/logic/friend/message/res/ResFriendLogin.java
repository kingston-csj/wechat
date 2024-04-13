package pers.kinson.wechat.logic.friend.message.res;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

/**
 * 好友登录
 *
 * @author kinson
 */
@Data
public class ResFriendLogin extends AbstractPacket {

    private long friendId;

    @Override
    public int getPacketType() {
        return CmdConst.ResFriendLogin;
    }

}
