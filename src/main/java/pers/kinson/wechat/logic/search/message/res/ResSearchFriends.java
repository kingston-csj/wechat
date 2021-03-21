package pers.kinson.wechat.logic.search.message.res;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.logic.search.model.RecommendFriendItem;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

import java.util.ArrayList;
import java.util.List;

public class ResSearchFriends extends AbstractPacket {

	private List<RecommendFriendItem> friends;

	@Override
	public int getPacketType() {
		return CmdConst.ResSearchFriends;
	}

	@Override
	public void writeBody(ByteBuf buf) {
		buf.writeInt(friends.size());
		for (RecommendFriendItem item:friends) {
			item.writeBody(buf);
		}

	}

	@Override
	public void readBody(ByteBuf buf) {
		int size = buf.readInt();
		this.friends = new ArrayList<>(size);
		for (int i=0;i<size;i++) {
			RecommendFriendItem item = new RecommendFriendItem();
			item.readBody(buf);
			friends.add(item);
		}
	}

	public List<RecommendFriendItem> getFriends() {
		return friends;
	}

	public void setFriends(List<RecommendFriendItem> friends) {
		this.friends = friends;
	}
}
