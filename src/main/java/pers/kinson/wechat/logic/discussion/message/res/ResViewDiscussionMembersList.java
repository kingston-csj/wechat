package pers.kinson.wechat.logic.discussion.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionMemberVo;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ResViewDiscussionMembers)
public class ResViewDiscussionMembersList {

    private Long discussionId;
    private List<DiscussionMemberVo> groups;
}
