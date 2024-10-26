package pers.kinson.wechat.net;

import lombok.Data;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class HttpResult extends AbstractPacket {

    /**
     * 0表示成功，非0代表错误
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 真正的消息，格式由业务自行定义
     */
    private String data;

    @Override
    public int getPacketType() {
        return CmdConst.ResCommon;
    }
}