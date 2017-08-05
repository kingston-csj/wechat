package com.kingston;

import com.kingston.transport.ChatClient;
import com.kingston.transport.ClientConfigs;

public class ClientStartup {

	public static void main(String[] args)  throws Exception{
		ChatClient.INSTANCE.connect(ClientConfigs.REMOTE_SERVER_IP,
								ClientConfigs.REMOTE_SERVER_PORT);
	}
}
