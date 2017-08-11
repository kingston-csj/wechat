package com.kingston.net.transport;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

import com.kingston.logic.login.message.ReqHeartBeatPacket;

public class HeartBeatHandler extends ChannelHandlerAdapter{

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
