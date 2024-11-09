package pers.kinson.wechat.logic.discussion.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.discussion.message.vo.DiscussionGroupVo;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ResViewDiscussionList)
public class ResViewDiscussionList {

    private List<DiscussionGroupVo> groups;
}
