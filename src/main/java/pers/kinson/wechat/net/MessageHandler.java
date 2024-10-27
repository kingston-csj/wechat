package pers.kinson.wechat.net;

@FunctionalInterface
public interface MessageHandler<T> {

    void action(T data);
}
