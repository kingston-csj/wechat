package pers.kinson.wechat.logic.discussion.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ReqJoinDiscussion)
public class ReqJoinDiscussion {

    private Long discussionId;

}
