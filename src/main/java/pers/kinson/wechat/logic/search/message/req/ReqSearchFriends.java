package pers.kinson.wechat.logic.search.message.req;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.ReqSearchFriends)
public class ReqSearchFriends  {

	/** 昵称或qq号 */
	private String key;

}
