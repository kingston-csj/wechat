package pers.kinson.wechat.logic.login.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqUserLogin)
public class ReqConnectSocket {

	private long userId;
	private String token;

}
