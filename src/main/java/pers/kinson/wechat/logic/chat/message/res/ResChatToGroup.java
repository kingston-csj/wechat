package pers.kinson.wechat.logic.chat.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResChatToGroup)
public class ResChatToGroup  {

	private String content;

}
