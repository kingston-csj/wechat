package pers.kinson.wechat.logic.user.message.res;

import lombok.Data;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

@Data

public class ResUserInfo extends AbstractPacket {

	private long userId;
	/** 账号昵称 */
	private String userName;
	/** 性别 */
	private byte sex;
	/** 个性签名　*/
	private String signature;

	@Override
	public int getPacketType() {
		return CmdConst.ResUserInfo;
	}

}
