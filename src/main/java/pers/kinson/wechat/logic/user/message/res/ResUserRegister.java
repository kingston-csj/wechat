package pers.kinson.wechat.logic.user.message.res;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data

public class ResUserRegister extends AbstractPacket {
	
	private byte resultCode;
	
	private String message;
	
	@Override
	public int getPacketType() {
		return CmdConst.ResUserRegister;
	}

}
