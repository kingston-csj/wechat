package pers.kinson.wechat.logic.chat.handler;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.FileMessageContent;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.util.ByteUnitConverter;
import pers.kinson.wechat.util.SchedulerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        FileMessageContent mediaMessageContent = (FileMessageContent) message.getMessageContent();

        // 创建一个垂直布局容器VBox，并设置一些间距和对齐方式
        VBox vBox = new VBox(10); // 设置子元素之间的垂直间距为10像素
        vBox.setAlignment(Pos.CENTER_LEFT); // 设置内容左对齐且垂直居中

        // 创建显示文件名的标签，并添加到VBox中
        Label nameUi = new Label(mediaMessageContent.getName());
        vBox.getChildren().add(nameUi);

        // 创建底部的水平布局容器HBox，并设置一些间距和对齐方式
        HBox bottom = new HBox(10); // 设置子元素之间的水平间距为10像素
        bottom.setAlignment(Pos.CENTER_LEFT); // 设置内容左对齐且垂直居中

        Label sizeLabel = new Label(ByteUnitConverter.convertBytesToUnit(mediaMessageContent.getSize()));
        sizeLabel.setStyle("-fx-font-size: 12px;"); // 设置字体大小为12像素，可根据需要调整

        // 创建下载按钮，并设置合适的样式和大小
        Button downloadBtn = new Button("save");
        downloadBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px;"); // 设置字体大小和按钮内边距

        // 将文件大小标签和下载按钮添加到HBox中
        bottom.getChildren().add(sizeLabel);
        bottom.getChildren().add(downloadBtn);

        // 将HBox添加到VBox中
        vBox.getChildren().add(bottom);

        // 设置VBox在父容器中的位置和间距
        vBox.setLayoutX(20);
        vBox.setLayoutY(20);

        downloadBtn.setOnAction(event -> {
            // 创建文件选择器
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择保存文件的位置");
            Stage stage = UiContext.stageController.getStageBy(R.Id.ChatContainer);
            // 弹出文件选择器对话框并获取用户选择的文件对象
            File selectedFile = fileChooser.showSaveDialog(parent.getScene().getWindow());

            if (selectedFile != null) {
                // 创建一个异步任务来执行文件保存操作，避免阻塞JavaFX主线程
                Task<Void> saveTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        InputStream inputStream;
                        FileOutputStream outputStream;
                        // 创建HTTP GET请求
                        HttpGet httpGet = new HttpGet(mediaMessageContent.getUrl());
                        // 执行请求并获取响应
                        HttpResponse response = httpClient.execute(httpGet);
                        // 获取响应实体
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            // 获取输入流以便读取文件内容
                            inputStream = entity.getContent();
                            // 创建输出流以便写入文件内容到本地
                            outputStream = new FileOutputStream(selectedFile);
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            // 关闭流
                            inputStream.close();
                            outputStream.close();
                        }
                        return null;
                    }
                };
                // 异步执行
                SchedulerManager.INSTANCE.runNow(saveTask);
            }
        });

        // 将VBox添加到父容器parent中
        parent.getChildren().add(vBox);
    }

    @Override
    public byte type() {
        return MessageContentType.FILE;
    }

}
