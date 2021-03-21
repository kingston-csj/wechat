package pers.kinson.wechat.logic.login.message.req;

import io.netty.buffer.ByteBuf;
import pers.kinson.wechat.net.CmdConst;
import pers.kinson.wechat.net.message.AbstractPacket;

public class ReqHeartBeat extends AbstractPacket {

	@Override
	public void writeBody(ByteBuf buf) {
	}

	@Override
	public void readBody(ByteBuf buf) {
		
	}

	@Override
	public int getPacketType() {
		return CmdConst.ReqHeartBeat;
	}

}
