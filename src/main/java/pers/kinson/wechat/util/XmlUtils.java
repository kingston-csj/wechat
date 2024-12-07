package pers.kinson.wechat.util;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Slf4j
public class XmlUtils {

    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class.getSimpleName());

    /**
     * 读取xml配置文件
     *
     * @param fileName
     * @param configClass
     * @return
     */
    public static <T> T loadXmlConfig(String fileName, Class<T> configClass) {
        T ob = null;
        if (!new File(fileName).exists()) {
            return ob;
        }
        Serializer serializer = new Persister();
        try {
            ob = serializer.read(configClass, new File(fileName));
        } catch (Exception ex) {
            logger.error("文件" + fileName + "配置有误", ex);
        }
        return ob;
    }

    public static void saveToFile(String fileName, Object obj) {
        Serializer serializer = new Persister();
        // 将Order对象序列化为XML文件并保存
        File result = new File(fileName);
        try {
            serializer.write(obj, result);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}