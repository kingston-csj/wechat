package pers.kinson.wechat.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.net.message.AbstractPacket;

public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {

    private Logger logger = LoggerFactory.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf out) throws Exception {
        try {
            //消息头
            out.writeInt(msg.getPacketType());
            //消息体
            byte[] msgBody = Context.messageCodec.encode(msg);
            out.writeInt(msgBody.length);
            out.writeBytes(msgBody);
        } catch (Exception e) {
            logger.error("", e);
        }
    }


}
