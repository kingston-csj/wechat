package pers.kinson.wechat.logic.redpoint;

import lombok.Builder;
import lombok.Data;
import pers.kinson.wechat.logic.redpoint.message.vo.RedPoint;

import java.util.Map;

@Data
@Builder
public class RedPointEvent {

    private Map<Integer, RedPoint> points;

}
