package pers.kinson.wechat.util;

import java.io.File;

public class FileUtil {

    public static void createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            // 获取父目录路径
            String parentPath = directory.getParent();
            if (parentPath != null) {
                createDirectory(parentPath);
            }
            boolean result = directory.mkdir();
            if (result) {
                System.out.println("目录 " + directoryPath + " 创建成功");
            } else {
                System.out.println("目录 " + directoryPath + " 创建失败");
            }
        }
    }
}
