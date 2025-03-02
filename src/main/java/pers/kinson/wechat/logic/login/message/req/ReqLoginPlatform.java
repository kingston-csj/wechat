package pers.kinson.wechat.logic.login.message.req;

import lombok.Data;

@Data
public class ReqLoginPlatform {

	private String username;
	private String password;

	private String grant_type = "password";

	private String client_id = "im_frontend";

	private String client_secret = "im_frontend_secret";

}
