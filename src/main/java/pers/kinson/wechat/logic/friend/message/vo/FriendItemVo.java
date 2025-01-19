package pers.kinson.wechat.logic.friend.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kinson.wechat.logic.chat.model.ChatContact;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.FriendVo)
public class FriendItemVo implements ChatContact {

    private long userId;
    /**
     * 在线状态
     */
    private byte online;
    /**
     * 昵称
     */
    private String userName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 个性签名
     */
    private String signature;
    /**
     * 　性别
     */
    private byte sex;
    /**
     * 所属好友分组
     */
    private int group;
    /**
     * 分组备注
     */
    private String groupName;
    /**
     * 头像地址
     */
    private String headUrl = "@../img/head.png";

    public boolean isOnline() {
        return online == Constants.ONLINE_STATUS;
    }

    public String getFullName() {
        if (StringUtils.isEmpty(remark)) {
            return this.userName;
        }
        return this.userName + "(" + this.remark + ")";
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public int getType() {
        return ChatContact.TYPE_FRIEND;
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public String getAvatar() {
        return headUrl;
    }

}

