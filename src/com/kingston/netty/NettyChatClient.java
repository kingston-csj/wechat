package com.kingston.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

import com.kingston.net.codec.PacketDecoder;
import com.kingston.net.codec.PacketEncoder;


public class NettyChatClient {

	public void connect(int port,String host) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b  = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel arg0)
						throws Exception {
					ChannelPipeline pipeline = arg0.pipeline();
					pipeline.addLast(new PacketDecoder(1024*4,0,4,0,4));
					pipeline.addLast(new LengthFieldPrepender(4));
					pipeline.addLast(new PacketEncoder());
					pipeline.addLast(new HeartBeatReqHandler()); 
					pipeline.addLast(new NettyClientHandler());
				}
				
			});
			
			ChannelFuture f = b.connect(host,port).sync();
			f.channel().closeFuture().sync();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}
	}
	
	
}
