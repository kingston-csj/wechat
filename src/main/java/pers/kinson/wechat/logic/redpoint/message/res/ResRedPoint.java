package pers.kinson.wechat.logic.redpoint.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.redpoint.message.vo.RedPoint;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ResRedPoint)
public class ResRedPoint {

    private List<RedPoint> points;
}