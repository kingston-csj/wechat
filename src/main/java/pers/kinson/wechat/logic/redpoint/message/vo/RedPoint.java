package pers.kinson.wechat.logic.redpoint.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.RedPointVo)
public class RedPoint {

    private int id;
    /**
     * 数量
     */
    private int count;
}
