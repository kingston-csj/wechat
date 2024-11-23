package pers.kinson.wechat.logic.user.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqUserRegister)
public class ReqUserRegister  {

	private long userId;
	/** 性别{@link Constants#SEX_OF_BOY} */
	private byte sex;

	private String nickName;

	private String password;

}
