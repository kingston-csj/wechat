package pers.kinson.wechat.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class FileUploadProgressMonitor {

    private ConcurrentMap<String, OnlineTransferInfo> datas = new ConcurrentHashMap<>();

    @Getter
    private static FileUploadProgressMonitor instance = new FileUploadProgressMonitor();

    public void register(String requestId, OnlineTransferInfo monitor) {
        datas.put(requestId, monitor);
    }

    public void clear(String requestId) {
        OnlineTransferInfo removed = datas.remove(requestId);
        if (removed != null) {
            NettyHttpServer httpServer = removed.getHttpServer();
            httpServer.shutdown();
            log.info("关闭http服务器{}", requestId);
        }
    }

    public OnlineTransferInfo getMonitor(String requestId) {
        return datas.get(requestId);
    }

}
