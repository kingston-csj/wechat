package pers.kinson.wechat;


import lombok.Data;
import org.simpleframework.xml.Element;

@Data
public class Client {
    @Element()
    private String version;
}
