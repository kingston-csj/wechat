package pers.kinson.wechat.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class SystemNotifyUtil {

    public static void warm(String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("系统提示");
        alert.setContentText(content);

        // 添加一个默认按钮（这里使用OK按钮作为示例）
        ButtonType okButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().add(okButton);

        // 设置点击默认按钮时关闭弹窗的行为
        alert.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return okButton;
            }
            return null;
        });

        alert.showAndWait();
    }
}
