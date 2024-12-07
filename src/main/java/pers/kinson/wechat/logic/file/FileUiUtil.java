package pers.kinson.wechat.logic.file;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import jforgame.commons.JsonUtil;
import jforgame.commons.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pers.kinson.wechat.SystemConfig;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.struct.FileMessageContent;
import pers.kinson.wechat.logic.chat.struct.ImageMessageContent;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.file.message.req.ReqOnlineTransferFileApply;
import pers.kinson.wechat.logic.file.message.res.ResUploadFile;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.ui.controller.ProgressFileEntity;
import pers.kinson.wechat.ui.controller.ProgressMonitor;
import pers.kinson.wechat.util.SchedulerManager;
import pers.kinson.wechat.util.SystemNotifyUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FileUiUtil {


    public static ResUploadFile uploadFile(File file, Map<String, String> params) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(SystemConfig.getInstance().getServer().getRemoteHttpUrl() + "/file/upload");
        // 使用MultipartEntityBuilder构建多部分表单
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("file", file.getName());
        builder.addTextBody("type", params.get("type"));
        builder.addTextBody("params", JsonUtil.object2String(params));
        HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);

        // 执行请求并获得响应
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        HttpResult httpResponse = JsonUtil.string2Object(EntityUtils.toString(entity), HttpResult.class);
        return JsonUtil.string2Object(httpResponse.getData(), ResUploadFile.class);
    }

    public static void sendImageResource(Window window, ReqChatToChannel request) throws IOException {
        FileChooser fileChooser = new FileChooser();
        // 设置文件选择器的标题和过滤器
        fileChooser.setTitle("选择图片");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // 显示文件选择器
        File file = fileChooser.showOpenDialog(window);
        if (file == null) {
            return;
        }
        sendImageResource(file, request);
    }

    public static void sendImageResource(File file, ReqChatToChannel request) throws IOException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                params.put("type", "1");

                // 执行请求
                try {
                    ResUploadFile resUploadFile = FileUiUtil.uploadFile(file, params);
                    HttpClient httpClient = HttpClientBuilder.create().build();
                    // 创建PUT请求
                    HttpPut httpPut = new HttpPut(resUploadFile.getPresignedUrl());
                    // 设置请求体为图片文件，并指定Content-type为image/jpeg
                    HttpEntity entity = new FileEntity(file, ContentType.IMAGE_JPEG);
                    httpPut.setEntity(entity);
                    HttpResponse response = httpClient.execute(httpPut);
                    ImageMessageContent content = new ImageMessageContent();
                    content.setUrl(resUploadFile.getCdnUrl());
                    request.setContent(JsonUtil.object2String(content));
                    request.setContentType(content.getType());

                    IOUtil.send(request);
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        };
        SchedulerManager.INSTANCE.runNow(task);
    }

    public static void sendFileResource(Window window, ReqChatToChannel request) throws IOException {
        FileChooser fileChooser = new FileChooser();
        // 设置文件选择器的标题和过滤器
        fileChooser.setTitle("选择文件");
        // 显示文件选择器
        File file = fileChooser.showOpenDialog(window);
        if (file == null) {
            return;
        }
        sendFileResource(window, file, request);
    }

    public static void sendFileResource(Window window, File file, ReqChatToChannel request) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        ResUploadFile resUploadFile = FileUiUtil.uploadFile(file, params);

        // 临时创建一个消息面板来显示文件进度
        VBox msgContainer = (VBox) window.getScene().getRoot().lookup("#msgContainer");
        StageController stageController = UiContext.stageController;
        Pane pane = stageController.load(R.layout.PrivateChatItemRight, Pane.class);
        FlowPane nameUi = (FlowPane) pane.lookup("#contentUi");
        ProgressBar progressBar = new ProgressBar();
        nameUi.getChildren().add(progressBar);
        msgContainer.getChildren().add(pane);

        // 使用javafx的task，执行进度刷新任务
        Task<Void> uploadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                HttpClient httpClient = HttpClientBuilder.create().build();
                // 创建PUT请求
                HttpPut httpPut = new HttpPut(resUploadFile.getPresignedUrl());
                ProgressMonitor monitor = new ProgressMonitor() {
                    @Override
                    public void updateTransferred(long changed) {
                        super.updateTransferred(changed);
                        updateProgress(getProgress(), getMaximum());
                    }
                };
                monitor.setMaximum(file.length());

                // 设置请求体为图片文件，并指定Content-type为image/jpeg
                HttpEntity entity = new ProgressFileEntity(monitor, file, ContentType.DEFAULT_BINARY);
                httpPut.setEntity(entity);
                // 执行请求
                HttpResponse response = httpClient.execute(httpPut);
                FileMessageContent content = new FileMessageContent();
                content.setName(file.getName());
                content.setSize(file.length());
                content.setUrl(resUploadFile.getCdnUrl());
                request.setContent(JsonUtil.object2String(content));
                request.setContentType(content.getType());

                IOUtil.send(request);
                return null;
            }
        };
        // 绑定进度条进度与任务进度
        progressBar.progressProperty().bind(uploadTask.progressProperty());
        SchedulerManager.INSTANCE.runNow(uploadTask);
    }

    public static void sendOnlineFileResource(Window window, Long receiverId) {
        FileChooser fileChooser = new FileChooser();
        // 设置文件选择器的标题和过滤器
        fileChooser.setTitle("选择文件");
        // 显示文件选择器
        File file = fileChooser.showOpenDialog(window);
        if (file == null) {
            return;
        }
        if (file.length() < Constants.MB_SIZE) {
            SystemNotifyUtil.warm("文件小于1M，直接发送即可");
            return;
        }
        if (file.length() > 500 * Constants.MB_SIZE) {
            SystemNotifyUtil.warm("文件大于500M，无法发送");
            return;
        }
        ReqOnlineTransferFileApply reqApply = new ReqOnlineTransferFileApply();
        reqApply.setFileSize(file.length());
        reqApply.setFileName(file.getName());
        reqApply.setFilePath(file.getAbsolutePath());
        reqApply.setReceiverId(receiverId);
        IOUtil.send(reqApply);
    }

    /**
     * 构建Downloads文件夹路径
     */
    public static String getDownloadPath(String fileName) {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads" + File.separator + fileName;
    }


    /**
     * 发送剪贴板内容
     *
     * @param output           文本显示框
     * @param reqChatToChannel 传送协议
     */
    public static void sendClipboardResource(TextArea output, ReqChatToChannel reqChatToChannel) {
        Pair<Byte, Object> dataFlavor = ClipboardUtil.getFromClipboard();
        byte dataType = dataFlavor.getFirst();
        switch (dataType) {
            case ClipboardUtil.TYPE_STRING:
                output.setText(dataFlavor.getSecond().toString());
                break;
            case ClipboardUtil.TYPE_IMAGE:
                File image = (File) dataFlavor.getSecond();
                try {
                    FileUiUtil.sendImageResource(image, reqChatToChannel);
                } catch (IOException e) {
                    log.error("", e);
                }
                break;
            case ClipboardUtil.TYPE_FILE:
                File file = (File) dataFlavor.getSecond();
                UiContext.runTaskInFxThread(() -> {
                    try {
                        FileUiUtil.sendFileResource(output.getScene().getWindow(), file, reqChatToChannel);
                    } catch (IOException e) {
                        log.error("", e);
                    }
                });
                break;
        }
    }

}
