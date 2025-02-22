package pers.kinson.wechat.config;

import lombok.Getter;
import lombok.Setter;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import pers.kinson.wechat.util.XmlUtils;

@Root(name = "app")
public class SystemConfig {

    private static volatile SystemConfig inst;

    @Getter
    @Setter
    @Element
    private Server server;

    @Getter
    @Setter
    @Element
    private Client client;

    @Getter
    @Setter
    @Element
    private Privacy privacy;

    public static SystemConfig getInstance() {
        if (inst != null) {
            return inst;
        }
        synchronized (SystemConfig.class) {
            if (inst == null) {
                SystemConfig.inst = XmlUtils.loadXmlConfig("system.xml", SystemConfig.class);
            }
        }
        return inst;
    }

    public void saveConfig() {
        XmlUtils.saveToFile("system.xml", SystemConfig.getInstance());
    }

}


