package com.kingston.chat.logic.user.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 用户个人信息
 * @author kingston
 */
public class UserModel {

	private long userId;
	/** 账号昵称 */
	private StringProperty userName = new SimpleStringProperty("");
	/** 个性签名 */
	private StringProperty signature = new SimpleStringProperty("");
	/** 性别 */
	private byte sex;

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public final StringProperty userNameProperty() {
		return userName;
	}
	public String getUserName() {
		return userName.get();
	}
	public void setUserName(String userName) {
		this.userName.set(userName);
	}
	public String getSignature() {
		return signature.get();
	}
	public void setSignature(String signature) {
		this.signature.set(signature);
	}
	public final StringProperty signaturePropertiy() {
		return this.signature;
	}

	public byte getSex() {
		return sex;
	}
	public void setSex(byte sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "MyProfile [userId=" + userId + ", userName=" + userName + ", signature=" + signature + ", sex=" + sex
				+ "]";
	}

}
