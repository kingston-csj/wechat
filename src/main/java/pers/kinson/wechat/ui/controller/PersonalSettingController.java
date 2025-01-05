package pers.kinson.wechat.ui.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jforgame.commons.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pers.kinson.wechat.SystemConfig;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.chat.message.vo.EmojiVo;
import pers.kinson.wechat.logic.file.message.res.ResUploadFile;
import pers.kinson.wechat.logic.system.AvatarCache;
import pers.kinson.wechat.logic.user.model.UserModel;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.SystemNotifyUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalSettingController implements ControlledStage {
    @FXML
    private TextField nameField, remarkField;
    @FXML
    private ImageView avatarImageView;

    private String avatarUrl;
    @FXML
    private Button saveButton;

    @FXML
    private GridPane avatarPane;


    private int avatarIndex;



    @Override
    public void onStageShown() {
        // 初始化头像图片
        UserModel profile = Context.userManager.getMyProfile();
        avatarUrl = profile.getAvatar();
        List<EmojiVo> avatarList = Context.settingManager.getAllAvatar();
        if (StringUtils.isEmpty(avatarUrl)) {
            avatarUrl = avatarList.get(0).getUrl();
        }
        avatarImageView.setImage(AvatarCache.getOrCreateImage(avatarUrl));
        nameField.setText(profile.getUserName());
        remarkField.setText(profile.getSignature());

        for (int i = 0; i < avatarList.size(); i++) {
            EmojiVo emojiVo = avatarList.get(i);
            if (emojiVo.getUrl().equals(avatarUrl)) {
                avatarIndex = i;
            }
        }
    }


    @FXML
    private void switchAvatar() throws IOException {
        avatarIndex++;
        List<EmojiVo> avatarList = Context.settingManager.getAllAvatar();
        if (avatarIndex == avatarList.size()) {
            avatarIndex = 0;
        }
        String url = avatarList.get(avatarIndex).getUrl();
        Image image = AvatarCache.getOrCreateImage(url);
        avatarImageView.setImage(image);
        avatarUrl = url;
        UserModel profile = Context.userManager.getMyProfile();
        profile.setAvatar(url);
    }

    private void chooseAvatar() {
        // 选择头像的逻辑
        // 创建一个文件选择器
        FileChooser fileChooser = new FileChooser();
        // 设置文件选择器的标题和过滤器
        fileChooser.setTitle("选择图片");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // 显示文件选择器
        File file = fileChooser.showOpenDialog(getMyStage());
        if (file == null) {
            return;
        }
        HttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(SystemConfig.getInstance().getServer().getRemoteHttpUrl() + "/file/upload");

        // 使用MultipartEntityBuilder构建多部分表单
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
        builder.addTextBody("type", "1");
        // 临时方法
        builder.addTextBody("params", "" + Context.userManager.getMyUserId());
        HttpEntity httpEntity = builder.build();
        httpPost.setEntity(httpEntity);

        // 执行请求并获得响应
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            try {
                HttpResult httpResponse = JsonUtil.string2Object(EntityUtils.toString(entity), HttpResult.class);
                ResUploadFile resUploadFile = JsonUtil.string2Object(httpResponse.getData(), ResUploadFile.class);
                avatarUrl = resUploadFile.getCdnUrl();
                avatarImageView.setImage(new Image(resUploadFile.getCdnUrl()));
                Context.userManager.getMyProfile().setAvatar(resUploadFile.getCdnUrl());
            } catch (Exception e) {
                e.printStackTrace();
//                LoggerUtil.error(String.format("json %s 解析错误", JsonUtil.object2String(entity)), e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void saveProfile() {
        // 保存个人资料的逻辑
        // 例如，更新数据库中的用户信息等
        String newName = nameField.getText();
        String newRemark = remarkField.getText();
        String newAvatar = avatarUrl;
        Map<String, Object> params = new HashMap<>();
        params.put("id", Context.userManager.getMyUserId());
        params.put("name", newName);
        params.put("remark", newRemark);
        params.put("avatar", newAvatar);
        try {
            HttpResult httpResult = Context.httpClientManager.post(SystemConfig.getInstance().getServer().getRemoteHttpUrl() + "/user/profile", params, HttpResult.class);
            if (httpResult.isOk()) {
                SystemNotifyUtil.warm("保存成功");
                Context.userManager.getMyProfile().setUserName(newName);
                Context.userManager.getMyProfile().setSignature(newRemark);
                Context.userManager.getMyProfile().setAvatar(avatarUrl);
                if (StringUtils.isNoneEmpty(newAvatar)) {
                    UiContext.runTaskInFxThread(() -> {
                        StageController stageController = UiContext.stageController;
                        // 刷新主页上的头像
                        Stage stage = stageController.getStageBy(R.id.MainView);
                        ImageView headImg = (ImageView) stage.getScene().getRoot().lookup("#headImg");
                        headImg.setImage(new Image(newAvatar));
                    });
                }
            } else {
                SystemNotifyUtil.warm("保存失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stage getMyStage() {
        StageController stageController = UiContext.stageController;
        return stageController.getStageBy(R.id.PersonSettingView);
    }

    @FXML
    private void close() {
        UiContext.stageController.closeStage(R.id.PersonSettingView);
    }

}