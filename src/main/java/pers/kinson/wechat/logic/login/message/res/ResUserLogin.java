package pers.kinson.wechat.logic.login.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResUserLogin)
public class ResUserLogin  {

	private String alertMsg;
	private byte isValid;
	
}
