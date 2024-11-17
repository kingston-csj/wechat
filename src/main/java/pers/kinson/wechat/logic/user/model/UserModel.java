package pers.kinson.wechat.logic.user.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户个人信息
 *
 * @author kinson
 */
@Getter
@Setter
public class UserModel {

    private long userId;
    /**
     * 账号昵称
     */
    private StringProperty userName = new SimpleStringProperty("");
    /**
     * 个性签名
     */
    private StringProperty signature = new SimpleStringProperty("");
    /**
     * 性别
     */
    private byte sex;
    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 私聊最大流水号
     */
    private long chatMaxSeq;


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

    public final StringProperty signatureProperty() {
        return this.signature;
    }


}
