package pers.kinson.wechat.logic.login.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ResSocketLogin)
public class ResConnectServer {

	private byte status;
	
}
