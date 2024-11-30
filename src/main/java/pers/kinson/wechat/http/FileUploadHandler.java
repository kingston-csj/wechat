package pers.kinson.wechat.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import jforgame.commons.TimeUtil;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.file.FileUiUtil;
import pers.kinson.wechat.util.Base64CodecUtil;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileUploadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 创建HttpDataFactory，用于创建HttpData对象
        HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
        // 创建HttpPostRequestDecoder，用于解析POST请求中的数据
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
        String requestId = request.headers().get("requestId");
        String secretKey = request.headers().get("secretKey");
        String fileName = Base64CodecUtil.decode(request.headers().get("fileName"));
        // 构建Downloads文件夹路径
        String downloadsPath = FileUiUtil.getDownloadPath(fileName);
        // 遍历解析出的所有HttpData对象
        List<InterfaceHttpData> httpDataList = decoder.getBodyHttpDatas();
        UiContext.runTaskInFxThread(() -> {
            for (InterfaceHttpData httpData : httpDataList) {
                if (httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    // 如果是文件上传类型的数据
                    FileUpload fileUpload = (FileUpload) httpData;
                    // 创建文件输出流
                    // 进度君
                    OnlineTransferInfo progressMonitor = FileUploadProgressMonitor.getInstance().getMonitor(requestId);
                    try (FileOutputStream fos = new FileOutputStream(downloadsPath)) {
                        ByteBuf content = fileUpload.getByteBuf();
                        byte[] buffer = new byte[1024]; // 定义一个字节数组作为缓冲区，大小可以根据实际情况调整
                        int bytesRead;
                        while ((bytesRead = content.readableBytes()) > 0) {
                            if (bytesRead > buffer.length) {
                                bytesRead = buffer.length;
                            }
                            content.readBytes(buffer, 0, bytesRead);
                            progressMonitor.getProgressMonitor().updateTransferred(bytesRead);
                            fos.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 延迟后清除资源
                SchedulerManager.INSTANCE.runDelay(() -> {
                    FileUploadProgressMonitor.getInstance().clear(requestId);
                }, TimeUtil.MILLIS_PER_SECOND);

            }
            // 发送响应表示文件接收成功
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.content().writeBytes("File uploaded successfully".getBytes(CharsetUtil.UTF_8));

            ctx.writeAndFlush(response);

        });
    }

}
