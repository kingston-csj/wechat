package pers.kinson.wechat.net;

import jforgame.codec.struct.StructMessageCodec;
import jforgame.commons.JsonUtil;
import jforgame.socket.client.RequestCallback;
import jforgame.socket.client.RpcMessageClient;
import jforgame.socket.client.SocketClient;
import jforgame.socket.netty.support.client.TcpSocketClient;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.SocketIoDispatcher;
import jforgame.socket.share.SocketIoDispatcherAdapter;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.RequestDataFrame;
import pers.kinson.wechat.base.Context;

public class IOUtil {

    private static SocketClient socketClient;

    public static MessageFactory messageFactory;

    public static void init() throws Exception {
        HostAndPort hostPort = new HostAndPort();
        hostPort.setHost(ClientConfigs.REMOTE_SERVER_IP);
        hostPort.setPort(ClientConfigs.REMOTE_SERVER_PORT);

        SocketIoDispatcher msgDispatcher = new SocketIoDispatcherAdapter() {
            @Override
            public void dispatch(IdSession session, Object frame) {
                RequestDataFrame dataFrame = (RequestDataFrame) frame;
                Object message = dataFrame.getMessage();
//                System.err.println("收到消息<-- " + message.getClass().getSimpleName() + "=" + JsonUtil.object2String(message));
                Context.messageRouter.execPacket(message);
            }
            @Override
            public void exceptionCaught(IdSession session, Throwable cause) {
                cause.printStackTrace();
            }
        };
        BaseMessageFactory messageFactory = new BaseMessageFactory("pers.kinson.wechat");
        SocketClient socketClient = new TcpSocketClient(msgDispatcher, messageFactory, new StructMessageCodec(), hostPort);
        IdSession session = socketClient.openSession();
        IOUtil.messageFactory = messageFactory;
        IOUtil.socketClient = socketClient;
    }

    public static <T> void callback(Object request, RequestCallback<T> callBack) {
        RpcMessageClient.callBack(socketClient.getSession(), request, callBack);
    }

    public static void send(Object message) {
        socketClient.getSession().send(message);
    }
}
