package pers.kinson.wechat.logic.chat.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.util.Map;


public class EmojiPopup extends PopupControl {


    private TextArea msgInput;

    public EmojiPopup(TextArea msgOutput) {
        this.msgInput = msgOutput;
        BorderPane borderPane = new BorderPane();

        // 创建包含表情的GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Map<String, EmojiVo> emojiVoMap = Context.chatManager.getEmojiVoMap();

        ObservableList<EmojiVo> emojis = FXCollections.observableArrayList(
                emojiVoMap.values()
        );

        int columnSum = 9;
        int columnIndex = 0;
        int rowIndex = 0;

        for (EmojiVo emoji : emojis) {
            ImageView imageView = new ImageView(emoji.getImage());
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            Tooltip tooltip = new Tooltip(emoji.getLabel());
            Tooltip.install(imageView, tooltip);
            imageView.setOnMouseClicked(event -> {
                System.out.println("clicked:" + emoji.getLabel());
                msgInput.setText(msgInput.getText() + "[" + emoji.getLabel() + "]");
                this.hide();
            });
            gridPane.add(imageView, columnIndex++, rowIndex);
            if (columnIndex == columnSum) {
                columnIndex = 0;
                rowIndex++;
            }
        }

        // 将GridPane设置到BorderPane的中心区域
        borderPane.setCenter(gridPane);

        // 设置样式
        borderPane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #ffffff;");
        StageController stageController = UiContext.stageController;
        Stage stage = stageController.getStageBy(R.id.ChatToPoint);
        Node root = stage.getScene().getRoot();
        // 为聊天容器添加鼠标点击事件过滤器
        root.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            if (!borderPane.contains(event.getX(), event.getY())) {
                this.hide();
            }
        });

        getScene().setRoot(borderPane);
    }

}