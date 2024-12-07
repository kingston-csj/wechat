package pers.kinson.wechat;

import lombok.Data;
import org.simpleframework.xml.Element;

@Data
public class Server {
    @Element()
    private String remoteServerIp;
    @Element()
    private int remoteServerPort;
    @Element()
    private String remoteHttpUrl;
}
