package pers.kinson.wechat.logic.redpoint;

import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.EventDispatcher;
import pers.kinson.wechat.base.LifeCycle;
import pers.kinson.wechat.logic.friend.message.res.ResFriendList;
import pers.kinson.wechat.logic.redpoint.message.res.ResRedPoint;
import pers.kinson.wechat.logic.redpoint.message.vo.RedPoint;
import pers.kinson.wechat.net.CmdConst;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RedPointManager implements LifeCycle {

    @Override
    public void init() {
        Context.messageRouter.registerHandler(CmdConst.ResRedPoint, this::onRedPointRefresh);
    }

    private void onRedPointRefresh(Object packet) {
        ResRedPoint resRedPoint = (ResRedPoint) packet;
        Map<Integer, RedPoint> map = resRedPoint.getPoints().stream().collect(Collectors.toMap(RedPoint::getId, Function.identity()));
        EventDispatcher.eventBus.post(new RedPointEvent.RedPointEventBuilder().points(map).build());
    }

}
