package pers.kinson.wechat.logic.friend.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Getter
@Setter
@MessageMeta(cmd = CmdConst.ResFriendOnlineStatus)
public class ResQueryFriendsOnlineStatus {

    private List<Long> ids;
}
