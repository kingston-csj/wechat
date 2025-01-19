package pers.kinson.wechat.logic.chat.model;

public interface ChatContact {

    int TYPE_FRIEND = 1;
    int TYPE_DISCUSSION = 2;

    Long getId();

    int getType();

    String getName();

    String getAvatar();

    default String getKey(){
        return getType() + "_" + getId();
    }

}
