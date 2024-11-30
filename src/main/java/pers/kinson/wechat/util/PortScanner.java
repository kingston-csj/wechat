package pers.kinson.wechat.util;

import java.io.IOException;
import java.net.ServerSocket;

public class PortScanner {

    /**
     * 寻找一个可用的socket端口，返回0代表暂时找不到
     */
    public static int nextPort() {
        int port = 5000; // 要检测的目标端口
        int counter = 0;
        for (; counter <= 10; counter++) {
            try {
                int result = port + counter;
                ServerSocket socket = new ServerSocket(result);
                socket.close();
                return result;
            } catch (IOException e) {
                System.out.println("端口 " + port + " 是关闭的或者无法访问。");
            }
            port++;
        }
        return 0;
    }

}