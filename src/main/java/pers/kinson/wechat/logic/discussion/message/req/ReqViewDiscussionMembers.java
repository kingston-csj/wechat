package pers.kinson.wechat.logic.discussion.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionMemberVo;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ReqViewDiscussionMembers)
public class ReqViewDiscussionMembers {

    private Long discussionId;

}