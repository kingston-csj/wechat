package pers.kinson.wechat.logic.search.message.res;

import jforgame.socket.share.annotation.MessageMeta;
import lombok.Data;
import pers.kinson.wechat.logic.search.model.RecommendFriendItem;
import pers.kinson.wechat.net.CmdConst;

import java.util.List;

@Data
@MessageMeta(cmd = CmdConst.ResSearchFriends)
public class ResSearchFriends  {

	private List<RecommendFriendItem> friends;

}
