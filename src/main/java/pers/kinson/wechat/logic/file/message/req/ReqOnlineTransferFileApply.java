package pers.kinson.wechat.logic.file.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqOnlineFileApply)
public class ReqOnlineTransferFileApply {

    /**
     * 接收方id
     */
    private Long receiverId;

    /**
     * 文件名称
     */
    private String fileName;


    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 发送方文件本地路径
     */
    private String filePath;
}
