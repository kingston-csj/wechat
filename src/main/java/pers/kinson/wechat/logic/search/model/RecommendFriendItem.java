package pers.kinson.wechat.logic.search.model;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.RecommendFriendVO)
public class RecommendFriendItem  {

    private long userId;

    private String nickName;

}
