package com.kingston.entry;

import com.kingston.netty.NettyChatClient;

public class StartClient {


	public static void main(String[] args)  throws Exception{
		int port = 8080;
		new NettyChatClient().connect(port, "127.0.0.1");
	}
}
