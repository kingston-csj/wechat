package com.kingston;

import com.kingston.net.transport.ChatClient;

public class ClientStartup {

	public static void main(String[] args) throws Exception{
		new ChatClient().start();
	}
}
