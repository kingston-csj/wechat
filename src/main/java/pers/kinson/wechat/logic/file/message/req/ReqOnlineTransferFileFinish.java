package pers.kinson.wechat.logic.file.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqOnlineFileFinish)
public class ReqOnlineTransferFileFinish {


    private Long messageId;

}
