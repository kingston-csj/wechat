package pers.kinson.wechat.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.net.message.AbstractPacket;

public class PacketDecoder extends LengthFieldBasedFrameDecoder {

    public PacketDecoder(int maxFrameLength, int lengthFieldOffset,
                         int lengthFieldLength, int lengthAdjustment,
                         int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment,
                initialBytesToStrip);
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        if (frame.readableBytes() <= 0) return null;

        int packetType = frame.readInt();
        AbstractPacket packet = Context.messageRouter.createNewPacket(packetType);
        int bodyLength = frame.readInt();    //先读压缩数据的长度
        byte[] sourceBytes = new byte[bodyLength];
        frame.readBytes(sourceBytes);

        return Context.messageCodec.decode(packet.getClass(), sourceBytes);
    }


}
