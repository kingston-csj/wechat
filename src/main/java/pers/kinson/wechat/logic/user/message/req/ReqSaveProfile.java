package pers.kinson.wechat.logic.user.message.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSaveProfile {

    private Long id;

    private String name;

    private String remark;

    private String avatar;
}
