package pers.kinson.wechat.net;

import jforgame.socket.client.RequestCallback;
import pers.kinson.wechat.logic.user.message.res.ResUserRegister;

public abstract class SimpleRequestCallback<T> implements RequestCallback<T> {

    @Override
    public abstract void onSuccess(T callBack);

    @Override
    public void onError(Throwable error) {

    }
}
