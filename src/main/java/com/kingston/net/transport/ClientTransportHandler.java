package com.kingston.net.transport;

import com.kingston.base.ServerManager;
import com.kingston.logic.login.LoginManager;
import com.kingston.net.PacketManager;
import com.kingston.net.message.AbstractPacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ClientTransportHandler extends ChannelHandlerAdapter{


	public ClientTransportHandler(){

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx){
		//注册session
		ServerManager.INSTANCE.registerSession(ctx.channel());
		//发送账号登录协议
		LoginManager.getInstance().beginToLogin();
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception{
		AbstractPacket  packet = (AbstractPacket)msg;
		PacketManager.INSTANCE.execPacket(packet);
	}

	@Override
	public void close(ChannelHandlerContext ctx,ChannelPromise promise){
		System.err.println("TCP closed...");
		ctx.close(promise);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.err.println("客户端关闭1");
	}

	@Override
	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		ctx.disconnect(promise);
		System.err.println("客户端关闭2");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("客户端关闭3");
		//	        ctx.fireExceptionCaught(cause);
		Channel channel = ctx.channel();
		cause.printStackTrace();
		if(channel.isActive()){
			System.err.println("simpleclient"+channel.remoteAddress()+"异常");
		}
	}
}
