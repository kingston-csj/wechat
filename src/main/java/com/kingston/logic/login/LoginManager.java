package com.kingston.logic.login;

import com.kingston.base.ServerManager;
import com.kingston.logic.login.message.ReqUserLoginPacket;
import com.kingston.logic.login.message.RespUserLoginPacket;

public class LoginManager {

	private static LoginManager instance = new  LoginManager();
	
	private LoginManager() {}
	
	public static LoginManager getInstance(){
		return instance;
	}
	
	public void beginToLogin() {
		ReqUserLoginPacket reqLogin= new ReqUserLoginPacket();  
		reqLogin.setUserId(1);
		reqLogin.setUserName("Netty爱好者");  
		reqLogin.setUserPwd("world");  
		System.err.println("向服务端发送登录请求");  
		
		ServerManager.INSTANCE.sendServerRequest(reqLogin);
	}
	
	public void receiveServerMsg(RespUserLoginPacket resp){
	}
}
