package pers.kinson.wechat.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import jforgame.commons.NumberUtil;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.req.ReqChatToChannel;
import pers.kinson.wechat.logic.chat.struct.TextMessageContent;
import pers.kinson.wechat.logic.chat.ui.EmojiPopup;
import pers.kinson.wechat.logic.constant.Constants;
import pers.kinson.wechat.logic.file.FileUiUtil;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.SchedulerManager;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatToPointController implements ControlledStage {

    @FXML
    private Label userIdUi;

    @FXML
    private TextArea msgInput;

    @FXML
    private Button sendBtn;


    @Override
    public void onStageShown() {
        msgInput.requestFocus();

        // 注册enter快捷键
        msgInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        // 注册ctrl+v快捷键
        // 复制系统剪贴板图片资源
        msgInput.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                SchedulerManager.INSTANCE.runNow(this::sendClipboardImage);
            }
        });
    }

    private void sendClipboardImage() {
        try {
            // 获取系统剪贴板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null) {
                DataFlavor[] flavors = contents.getTransferDataFlavors();
                for (DataFlavor flavor : flavors) {
                    if (flavor.getMimeType().contains("application/x-java-file-list")) {
                        // 尝试从文件列表格式中获取图像文件路径
                        Object data = contents.getTransferData(flavor);
                        if (data instanceof List) {
                            List<?> fileList = (List<?>) data;
                            for (Object item : fileList) {
                                if (item instanceof File) {
                                    File file = (File) item;
                                    if (file.isFile()) {
                                        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
                                        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
                                        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));
                                        if (isImageFile(file)) {
                                            // 读取图像文件并进行处理
//                                                BufferedImage awtImage = ImageIO.read(file);
                                            // 将BufferedImage转换为JavaFX Image对象
//                                                javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(awtImage, null);
//                                                System.out.println("Image pasted (size: " + fxImage.getWidth() + "x" + fxImage.getHeight() + ")");


                                            FileUiUtil.sendImageResource(file, reqChatToChannel);
                                        } else {
                                            UiContext.runTaskInFxThread(() -> {
                                                try {
                                                    FileUiUtil.sendFileResource(getMyStage(), file, reqChatToChannel);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isImageFile(File file) {
        String fileName = file.getName();
        String[] imageExtensions = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};
        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void sendMessage() {
        final long userId = Long.parseLong(userIdUi.getText());
        String message = msgInput.getText();
        TextMessageContent content = new TextMessageContent();
        content.setContent(message);
        Context.chatManager.sendMessageTo(userId, content);
        msgInput.clear();
    }


    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.ChatToPoint);
    }

    @FXML
    private void close() {
        UiContext.stageController.closeStage(R.id.ChatToPoint);
        Context.friendManager.resetActivatedFriendId();
    }

    @FXML
    private void createDiscussion() {
        StageController stageController = UiContext.stageController;
        stageController.setStage(R.id.CreateDiscussion);
    }

    @FXML
    private void showFacePanel() {
        EmojiPopup emojiPopup = new EmojiPopup(msgInput);
        emojiPopup.show(getMyStage().getScene().getWindow());
    }

    @FXML
    private void sendImageResource() throws IOException {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));

        FileUiUtil.sendImageResource(getMyStage(), reqChatToChannel);
    }

    @FXML
    private void sendOfflineFileResource() throws IOException {
        ReqChatToChannel reqChatToChannel = new ReqChatToChannel();
        reqChatToChannel.setChannel(Constants.CHANNEL_PERSON);
        reqChatToChannel.setTarget(NumberUtil.longValue(userIdUi.getText()));

        FileUiUtil.sendFileResource(getMyStage(), reqChatToChannel);
    }

    @FXML
    private void sendOnlineFileResource() throws IOException {
        FileUiUtil.sendOnlineFileResource(getMyStage(), NumberUtil.longValue(userIdUi.getText()));
    }


}


