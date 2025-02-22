package pers.kinson.wechat.config;


import lombok.Data;
import org.simpleframework.xml.Element;

@Data
public class Client {
    @Element()
    private String version;
}
