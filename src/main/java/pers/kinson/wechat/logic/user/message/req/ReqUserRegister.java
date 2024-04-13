package pers.kinson.wechat.logic.user.message.req;

import lombok.Data;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data
public class ReqUserRegister extends AbstractPacket {

	private long userId;
	/** 性别{@link Constants#SEX_OF_BOY} */
	private byte sex;

	private String nickName;

	private String password;

	@Override
	public int getPacketType() {
		return CmdConst.ReqUserRegister;
	}

}
