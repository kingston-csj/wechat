package pers.kinson.wechat.logic.login.message.res;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data

public class ResUserLogin extends AbstractPacket {

	private String alertMsg;
	private byte isValid;
	
	@Override
	public int getPacketType() {
		return CmdConst.ResUserLogin;
	}


}
