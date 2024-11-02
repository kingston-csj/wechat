package pers.kinson.wechat.logic.friend.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.friend.message.vo.FriendApplyVo;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ResApplyFriendList)
public class ResApplyFriendList {

    List<FriendApplyVo> records;
}
