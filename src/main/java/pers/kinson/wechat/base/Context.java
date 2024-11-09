package pers.kinson.wechat.base;

import jforgame.codec.MessageCodec;
import jforgame.codec.struct.StructMessageCodec;
import jforgame.socket.client.SocketClient;
import pers.kinson.wechat.logic.chat.ChatManager;
import pers.kinson.wechat.logic.discussion.DiscussionManager;
import pers.kinson.wechat.logic.friend.FriendManager;
import pers.kinson.wechat.logic.login.LoginManager;
import pers.kinson.wechat.logic.redpoint.RedPointManager;
import pers.kinson.wechat.logic.user.UserManager;
import pers.kinson.wechat.net.MessageRouter;
import pers.kinson.wechat.util.HttpClientManager;

import java.util.Arrays;

public class Context {

    public static LoginManager loginManager;

    public static UserManager userManager;

    public static FriendManager friendManager;

    public static MessageCodec messageCodec = new StructMessageCodec();

    public static MessageRouter messageRouter;

    public static RedPointManager redPointManager;

    public static ChatManager chatManager;

    public static DiscussionManager discussionManager;

    public static HttpClientManager httpClientManager;

    public static void init() {
        Arrays.stream(Context.class.getDeclaredFields()).forEach(
                f -> {
                    try {
                        if (f.get(null) == null) {
                            Object obj = f.getType().newInstance();
                            f.set(null, obj);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        Arrays.stream(Context.class.getDeclaredFields()).forEach(
                f -> {
                    try {
                        Object obj = f.get(null);
                        if (LifeCycle.class.isAssignableFrom(f.getType())) {
                            ((LifeCycle) obj).init();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}
