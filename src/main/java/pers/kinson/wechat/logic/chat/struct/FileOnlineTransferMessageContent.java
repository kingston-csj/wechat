package pers.kinson.wechat.logic.chat.struct;

import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.base.MessageContentType;

@Getter
@Setter
public class FileOnlineTransferMessageContent extends MediaMessageContent {

    public static final byte STATUS_APPLY = 0;
    public static final byte STATUS_REJECT = 1;
    public static final byte STATUS_DOING = 2;
    public static final byte STATUS_OK = 3;

    private String requestId;

    private String name;

    private String fileUrl;

    private long size;

    private Long fromId;

    private Long toId;

    /**
     * 状态
     */
    private byte status;

    public FileOnlineTransferMessageContent() {
        setType(MessageContentType.ONLINE_TRANSFER);
    }
}
