package pers.kinson.wechat.net;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;

@Data
@MessageMeta(cmd = CmdConst.ResCommon)
public class HttpResult  {

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

}