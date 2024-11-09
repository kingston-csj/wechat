package pers.kinson.wechat.logic.discussion.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqViewDiscussionList)
public class ReqViewDiscussionList {

}
