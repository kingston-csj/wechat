package pers.kinson.wechat.logic.login.message.req;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class ReqUserLogin extends AbstractPacket {

	private long userId;
	private String userPwd;

	@Override
	public int getPacketType() {
		return CmdConst.ReqUserLogin;
	}


}
