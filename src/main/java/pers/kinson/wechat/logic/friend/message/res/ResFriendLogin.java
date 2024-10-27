package pers.kinson.wechat.logic.friend.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

/**
 * 好友登录
 */
@Data
@MessageMeta(cmd = CmdConst.ResFriendLogin)
public class ResFriendLogin  {

    private long friendId;

}
