package pers.kinson.wechat.logic.discussion.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.net.CmdConst;

@Getter
@Setter
@MessageMeta(cmd = CmdConst.DiscussionGroupMemberVo)
public class DiscussionMemberVo {

    private Long id;

    private Long userId;

    private String nickName;


    /**
     * 职位
     */
    private byte position;

    private byte online;
}