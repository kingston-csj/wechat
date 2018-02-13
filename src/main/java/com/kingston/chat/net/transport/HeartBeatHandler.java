package com.kingston.chat.net.transport;

import java.util.concurrent.TimeUnit;

import com.kingston.chat.logic.login.message.req.ReqHeartBeatPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter  {

	private volatile ScheduledFuture<?> heartBeatScheduler;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception{
		if(heartBeatScheduler == null){
			System.err.println("init heartBeatScheduler---------");
			heartBeatScheduler = ctx.executor().scheduleAtFixedRate(
					new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
		}
		ctx.fireChannelRead(msg);

	}

	private class HeartBeatTask implements Runnable{
		private final ChannelHandlerContext ctx;

		public HeartBeatTask(final ChannelHandlerContext ctx){
			this.ctx = ctx;

		}
		@Override
		public void run() {
			this.ctx.writeAndFlush(new ReqHeartBeatPacket());
		}

	}
}
