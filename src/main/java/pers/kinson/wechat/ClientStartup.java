package pers.kinson.wechat;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pers.kinson.wechat.base.Context;
import pers.kinson.wechat.base.UiContext;
import pers.kinson.wechat.net.IOUtil;
import pers.kinson.wechat.ui.R;
import pers.kinson.wechat.ui.StageController;
import pers.kinson.wechat.util.IdFactory;

import java.io.File;
import java.io.IOException;

public class ClientStartup extends Application {

    @Override
    public void init() throws Exception {

        Context.init();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        //与服务端建立连接
        connectToServer();
        StageController stageController = UiContext.stageController;
        stageController.setPrimaryStage("root", stage);

        Stage loginStage = stageController.loadStage(R.id.LoginView, R.layout.LoginView,
                StageStyle.UNDECORATED);
        loginStage.setTitle("QQ");

        stageController.loadStage(R.id.RegisterView, R.layout.RegisterView, StageStyle.UNDECORATED);
        Stage mainStage = stageController.loadStage(R.id.MainView, R.layout.MainView, StageStyle.UTILITY);
        stageController.loadStage(R.id.PersonSettingView, R.layout.PersonSettingView, StageStyle.UNDECORATED);

        //把主界面放在右上方
        Screen screen = Screen.getPrimary();
        double rightTopX = screen.getVisualBounds().getWidth() * 0.75;
        double rightTopY = screen.getVisualBounds().getHeight() * 0.05;
        mainStage.setX(rightTopX);
        mainStage.setY(rightTopY);

        stageController.loadStage(R.id.ChatToPoint, R.layout.ChatToPoint, StageStyle.UTILITY);
        stageController.loadStage(R.id.CreateDiscussion, R.layout.CreateDiscussion, StageStyle.UTILITY);
        stageController.loadStage(R.id.DiscussionGroup, R.layout.DiscussionGroup, StageStyle.UTILITY);

        Stage searchStage = stageController.loadStage(R.id.SearchView, R.layout.SearchFriendView,
                StageStyle.UTILITY);

        //显示MainView舞台
        stageController.setStage(R.id.LoginView);
//		stageController.setStage(R.id.SearchView);
    }

    private void test() throws IOException {
        // 创建HttpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建HttpPost请求，指定服务器地址和端口
        HttpPost httpPost = new HttpPost("http://localhost:10086");

        // 构建要上传的文件实体
        File fileToUpload = new File("test.txt"); // 替换为实际要上传的文件路径
        HttpEntity fileEntity = MultipartEntityBuilder.create()
                .addBinaryBody("file", fileToUpload, ContentType.APPLICATION_OCTET_STREAM, fileToUpload.getName())
                .build();

        // 设置请求实体
        httpPost.setEntity(fileEntity);

        // 设置请求头，这里设置文件名，与服务器端接收时获取文件名的方式对应
        httpPost.setHeader("requestId", IdFactory.nextUUId());

        // 发送请求并获取响应
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity!= null) {
                System.out.println("Response content: " + org.apache.http.util.EntityUtils.toString(responseEntity));
            }
        } finally {
            // 关闭资源
            response.close();
            httpClient.close();
        }
    }

    private void connectToServer() {
        new Thread() {
            @SneakyThrows
            public void run() {
                IOUtil.init();
            }

        }.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
