package pers.kinson.wechat.logic.search.model;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class RecommendFriendItem extends AbstractPacket {

    private long userId;

    private String nickName;

    @Override
    public int getPacketType() {
        return CmdConst.RecommendFriendVO;
    }
}
