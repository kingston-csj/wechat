package pers.kinson.wechat.logic.discussion.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.chat.model.ChatContact;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.DiscussionGroupVo)
public class DiscussionGroupVo implements ChatContact {

    private Long id;

    private long maxSeq;

    private String name;

    private String avatar;

    @Override
    public int getType() {
        return ChatContact.TYPE_DISCUSSION;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

}
