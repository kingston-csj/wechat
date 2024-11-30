package pers.kinson.wechat.logic.chat.handler;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.http.FileUploadProgressMonitor;
import pers.kinson.wechat.http.NettyHttpServer;
import pers.kinson.wechat.http.OnlineTransferInfo;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.FileOnlineTransferMessageContent;
import pers.kinson.wechat.logic.file.FileUiUtil;
import pers.kinson.wechat.logic.file.message.req.ReqOnlineTransferFileAnswer;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.controller.ProgressMonitor;
import pers.kinson.wechat.util.ByteUnitConverter;
import pers.kinson.wechat.util.IdFactory;
import pers.kinson.wechat.util.IpAddressUtil;
import pers.kinson.wechat.util.PortScanner;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

@Slf4j
public class OnlineTransferContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        FileOnlineTransferMessageContent transferMessageContent = (FileOnlineTransferMessageContent) message.getContent();
        // 创建一个垂直布局容器VBox，并设置一些间距和对齐方式
        VBox vBox = new VBox(10); // 设置子元素之间的垂直间距为10像素
        vBox.setAlignment(Pos.CENTER_LEFT); // 设置内容左对齐且垂直居中

        // 创建显示文件名的标签，并添加到VBox中
        Label nameUi = new Label(transferMessageContent.getName() + "(" + ByteUnitConverter.convertBytesToUnit(transferMessageContent.getSize()) + ")");
        vBox.getChildren().add(nameUi);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setId("progressBar");
        progressBar.setVisible(false);
        vBox.getChildren().add(progressBar);

        // 创建底部的水平布局容器HBox，并设置一些间距和对齐方式
        HBox bottom = new HBox(10); // 设置子元素之间的水平间距为10像素
        bottom.setAlignment(Pos.CENTER_LEFT); // 设置内容左对齐且垂直居中

        Label statusUi = new Label();
        statusUi.setId("status");
        vBox.getChildren().add(statusUi);

        Button agreeBtn = new Button("同意");
        agreeBtn.setId("agreeBtn");
        agreeBtn.setVisible(false);
        agreeBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px;");
        agreeBtn.setOnMouseClicked(evt -> {
            ReqOnlineTransferFileAnswer reqAnswer = new ReqOnlineTransferFileAnswer();
            String ipHost = IpAddressUtil.getOuterIpAddress();
            int port = PortScanner.nextPort();
            if (port < 0) {
                log.error("无法启动http服务器");
                return;
            }
            OnlineTransferInfo node = new OnlineTransferInfo();
            node.setSecretKey(IdFactory.nextUUId());
            NettyHttpServer httpServer = new NettyHttpServer();
            node.setHttpServer(httpServer);
            new Thread(() -> {
                try {
                    log.info("启动http服务器@" + ipHost + ":" + port);
                    httpServer.start(ipHost, port);
                } catch (Exception e) {
                    log.error("", e);
                }
            }).start();
            node.setProgressMonitor(new ProgressMonitor());

            reqAnswer.setMessageId(message.getId());
            reqAnswer.setHost(String.format("http://%s:%d", ipHost, port));
            reqAnswer.setSecretKey(node.getSecretKey());
            reqAnswer.setStatus(FileOnlineTransferMessageContent.STATUS_DOING);
            reqAnswer.setFileName(transferMessageContent.getName());
            reqAnswer.setFileUrl(transferMessageContent.getFileUrl());

            progressBar.setVisible(true);
            progressBar.progressProperty().bind(node.getProgressMonitor().getProgressProperty());

            FileUploadProgressMonitor.getInstance().register("" + message.getId(), node);

            IOUtil.send(reqAnswer);
        });

        Button rejectBtn = new Button("拒绝");
        rejectBtn.setVisible(false);
        rejectBtn.setId("rejectBtn");
        rejectBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px;");
        rejectBtn.setOnMouseClicked(evt -> {
            ReqOnlineTransferFileAnswer reqAnswer = new ReqOnlineTransferFileAnswer();
            reqAnswer.setMessageId(message.getId());
            IOUtil.send(reqAnswer);
        });

        Button pauseBtn = new Button("中断");
        pauseBtn.setId("pauseBtn");
        pauseBtn.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px;");
        pauseBtn.setVisible(false);


        Button viewBtn = new Button("查看");
        viewBtn.setId("viewBtn");
        viewBtn.setVisible(false);

        // 将文件大小标签和下载按钮添加到HBox中
        bottom.getChildren().add(agreeBtn);
        bottom.getChildren().add(rejectBtn);
        bottom.getChildren().add(pauseBtn);
        bottom.getChildren().add(viewBtn);

        // 将HBox添加到VBox中
        vBox.getChildren().add(bottom);

        // 设置VBox在父容器中的位置和间距
        vBox.setLayoutX(20);
        vBox.setLayoutY(20);

        parent.getChildren().add(vBox);
        refresh(vBox, message);
    }

    @Override
    public byte type() {
        return MessageContentType.ONLINE_TRANSFER;
    }

    @Override
    public void refresh(Pane parent, ChatMessage message) {
        Label statusUi = (Label) parent.lookup("#status");
        FileOnlineTransferMessageContent transferMessageContent = (FileOnlineTransferMessageContent) message.getContent();
        long myUserId = Context.userManager.getMyUserId();
        if (transferMessageContent.getStatus() == FileOnlineTransferMessageContent.STATUS_APPLY) {
            if (myUserId == transferMessageContent.getFromId()) {
                statusUi.setText("等待对方应答");
            } else {
                statusUi.setText("对方申请向你传输文件，是否接受");
                parent.lookup("#agreeBtn").setVisible(true);
                parent.lookup("#rejectBtn").setVisible(true);
            }
        } else if (transferMessageContent.getStatus() == FileOnlineTransferMessageContent.STATUS_REJECT) {
            if (myUserId == transferMessageContent.getFromId()) {
                statusUi.setText("对方拒绝");
            } else {
                statusUi.setText("已拒绝");
            }
        } else if (transferMessageContent.getStatus() == FileOnlineTransferMessageContent.STATUS_DOING) {
            statusUi.setText("文件传输中");
            parent.lookup("#agreeBtn").setVisible(false);
            parent.lookup("#rejectBtn").setVisible(false);
            parent.lookup("#pauseBtn").setVisible(true);
        } else if (transferMessageContent.getStatus() == FileOnlineTransferMessageContent.STATUS_OK) {
            statusUi.setText("传输完成");
            parent.lookup("#pauseBtn").setVisible(false);
            parent.lookup("#progressBar").setVisible(false);
            Node viewBtn = parent.lookup("#viewBtn");
            viewBtn.setVisible(true);
            viewBtn.setOnMouseClicked(evt -> {
                String downloadPath = FileUiUtil.getDownloadPath(transferMessageContent.getName());
                // 发起方，路径不一样
                if (myUserId == transferMessageContent.getFromId()) {
                    downloadPath = transferMessageContent.getFileUrl();
                }
//                File file = new File(downloadPath);
//                if (Desktop.isDesktopSupported()) {
//                    Desktop desktop = Desktop.getDesktop();
//                    if (file.exists()) {
//                        // 获取文件所在的父目录(如果不用父母目录，则会直接打开)
//                        File parentDirectory = file.getParentFile();
//                        try {
//                            desktop.browse(parentDirectory.toURI());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

                try {
                    // 要打开的目录路径
                    String directoryPath = "C:\\Users\\Public";
                    // 构建命令字符串，在Windows中使用explorer.exe打开目录
                    String command = "explorer.exe /select, " + downloadPath;
                    // 执行命令
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}


