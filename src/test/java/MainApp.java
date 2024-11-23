import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        FlowPane flowPane = new FlowPane();
        TextField text = new TextField("这是一段可选择的文本");
        text.setEditable(false); // 设置为true以允许编辑文本
        text.setMouseTransparent(false);
        text.setFocusTraversable(true);
        text.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-padding: 0;");
        flowPane.getChildren().add(text);

        Scene scene = new Scene(flowPane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}