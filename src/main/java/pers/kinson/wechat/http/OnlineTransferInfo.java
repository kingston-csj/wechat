package pers.kinson.wechat.http;

import lombok.Getter;
import lombok.Setter;
import pers.kinson.wechat.ui.controller.ProgressMonitor;

@Getter
@Setter
public class OnlineTransferInfo {

    NettyHttpServer httpServer;

    ProgressMonitor progressMonitor;

    String secretKey;

}
