package pers.kinson.wechat.logic.friend.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.net.CmdConst;

@Getter
@Setter
@MessageMeta(cmd = CmdConst.ReqFriendOnlineStatus)
public class ReqQueryFriendsOnlineStatus {

}
