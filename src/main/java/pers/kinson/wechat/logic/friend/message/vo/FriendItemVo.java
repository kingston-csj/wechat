package pers.kinson.wechat.logic.friend.message.vo;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pers.kinson.wechat.base.Constants;
import pers.kinson.wechat.net.CmdConst;

@Data
@MessageMeta(cmd = CmdConst.FriendVo)
public class FriendItemVo  {

	private long userId;
	/** 在线状态 {@link Constants#online_status} */
	private byte online;
	/** 昵称 */
	private String userName;
	/** 备注 */
	private String remark;
	/** 个性签名　*/
	private String signature;
	/**　性别 */
	private byte sex;
	/** 所属好友分组 */
	private int group;
	/** 分组备注 */
	private String groupName;

	public boolean isOnline() {
		return online == Constants.ONLINE_STATUS;
	}

	public String getFullName() {
		if (StringUtils.isEmpty(remark)) {
			return this.userName;
		}
		return this.userName + "(" + this.remark + ")";
	}

}

