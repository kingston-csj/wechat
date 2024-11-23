package pers.kinson.wechat.ui.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.logic.file.ResUploadFile;
import pers.kinson.wechat.logic.user.model.UserModel;
import pers.kinson.wechat.net.ClientConfigs;
import pers.kinson.wechat.net.HttpResult;
import pers.kinson.wechat.ui.ControlledStage;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PersonalSettingController implements ControlledStage, Initializable {
    @FXML
    private TextField nameField, remarkField;
    @FXML
    private ImageView avatarImageView;

    private String avatarUrl;
    @FXML
    private Button saveButton;

    @Override
    public void onStageShown() {
        // 初始化头像图片
        UserModel profile = Context.userManager.getMyProfile();
        avatarUrl = profile.getAvatar();
        if (StringUtils.isEmpty(avatarUrl)) {
            avatarImageView.setImage(new Image("@../../login/img/headimag.png"));
        } else {
            avatarImageView.setImage(new Image(avatarUrl));
        }
        nameField.setText(profile.getUserName());
        remarkField.setText(profile.getSignature());
    }

    @FXML
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

        HttpPost httpPost = new HttpPost(ClientConfigs.REMOTE_HTTP_SERVER + "/file/upload");

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
            HttpResult httpResult = Context.httpClientManager.post(ClientConfigs.REMOTE_HTTP_SERVER + "/user/profile", params, HttpResult.class);
            if (httpResult.isOk()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("信息");
                alert.setContentText("保存成功");
                alert.showAndWait();

                Context.userManager.getMyProfile().setUserName(newName);
                Context.userManager.getMyProfile().setSignature(newRemark);
                if (StringUtils.isNoneEmpty(newAvatar)) {
                    StageController stageController = UiContext.stageController;
                    // 刷新主页上的头像
                    Stage stage = stageController.getStageBy(R.id.MainView);
                    ImageView headImg = (ImageView) stage.getScene().getRoot().lookup("#headImg");
                    headImg.setImage(new Image(newAvatar));
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("警告");
                alert.setContentText("保存失败");
                alert.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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