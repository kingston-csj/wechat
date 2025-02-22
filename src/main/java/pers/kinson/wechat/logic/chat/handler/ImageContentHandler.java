package pers.kinson.wechat.logic.chat.handler;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pers.kinson.wechat.base.MessageContentType;
import pers.kinson.wechat.logic.chat.message.vo.ChatMessage;
import pers.kinson.wechat.logic.chat.struct.MediaMessageContent;

public class ImageContentHandler implements MessageContentUiHandler {

    @Override
    public void display(Pane parent, ChatMessage message) {
        MediaMessageContent mediaMessageContent = (MediaMessageContent) message.getMessageContent();
        Image image = new Image(mediaMessageContent.getUrl());
        ImageView imageView = new ImageView(image);
        int MAX_WIDTH = 480;
        int MAX_HEIGHT = 480;
        // 比较图像原始宽度和最大宽度
        if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
            imageView.setFitHeight(480);
            imageView.setFitWidth(480);
            // 保持图像的宽高比
            imageView.setPreserveRatio(true);
        }

        // 创建新的Stage来显示原始尺寸的图像
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            Image newImage = new Image(mediaMessageContent.getUrl());
            // 根据原始图像尺寸设置新窗口大小
            double imageWidth = newImage.getWidth();
            double imageHeight = newImage.getHeight();
            // 获取主屏幕对象
            Screen screen = Screen.getPrimary();
            // 获取屏幕的可视边界

            Rectangle2D visualBounds = screen.getVisualBounds();
            // 获取屏幕的最大宽度和高度
            double screenWidth = visualBounds.getWidth();
            double screenHeight = visualBounds.getHeight();
            ImageView originalImageView = new ImageView(newImage);
            if (imageWidth > screenWidth) {
                imageWidth = screenWidth;
                originalImageView.setFitWidth(imageWidth);
            }
            if (imageHeight > screenHeight) {
                imageHeight = screenHeight;
                originalImageView.setFitHeight(imageHeight);
            }

            VBox originalVBox = new VBox(originalImageView);
            Scene originalScene = new Scene(originalVBox, imageWidth + 20, imageHeight + 20);
            newStage.setScene(originalScene);
            newStage.show();
        });

        parent.getChildren().add(imageView);
    }

    @Override
    public byte type() {
        return MessageContentType.IMAGE;
    }

}
