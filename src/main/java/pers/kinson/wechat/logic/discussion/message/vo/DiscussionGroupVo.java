package pers.kinson.wechat.logic.discussion.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.DiscussionGroupVo)
public class DiscussionGroupVo {

    private Long id;

    private long maxSeq;

    private String name;
}
