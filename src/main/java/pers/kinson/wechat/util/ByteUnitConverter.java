package pers.kinson.wechat.util;

import java.text.DecimalFormat;

public class ByteUnitConverter {

    /**
     * 根据字节数自动转换为合适的存储单位（MB或GB）并格式化输出
     *
     * @param bytes 要转换的字节数
     * @return 格式化后的存储单位及数值字符串
     */
    public static String convertBytesToUnit(long bytes) {
        double mbValue = bytes / (1024.0 * 1024.0);
        double gbValue = bytes / (1024.0 * 1024.0 * 1024.0);

        if (gbValue >= 1) {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(gbValue) + "GB";
        } else if (mbValue >= 1) {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(mbValue) + "MB";
        } else {
            return bytes + "B";
        }
    }
}
