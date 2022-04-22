package com.source;

import com.source.common.AbstractJavaFxApplicationSupport;
import com.source.controller.MainController;
import com.source.view.MainView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Source
 * @date 2022/04/04/22:49
 */
@SpringBootApplication
public class Boot extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(Boot.class, MainView.class, args);
    }

//    public static void main(String[] args) {
//        launch(args);
//    }

    @Override
    public void start(Stage primaryStage) throws IOException {
//        InputStream downloadImage = HttpUtil.doDownloadAvatar(qq);
//        assert downloadImage != null;
//        image = new Image(downloadImage);
//        ImageView imageView = new ImageView(image);
//        imageView.setLayoutX(5);
//        imageView.setLayoutY(5);
//        Label label = new Label(qq);
//        label.setLayoutX(5);
//        label.setLayoutY(150);
//        label.setPrefWidth(image.getWidth());
//        label.setAlignment(Pos.CENTER);
//        label.setStyle("-fx-border-width: 1; -fx-border-color: black;");
//        AnchorPane pane = new AnchorPane();
//        pane.setPadding(new Insets(10, 10, 10, 10));
//        pane.getChildren().add(label);
//        pane.getChildren().add(imageView);
//        Scene scene = new Scene(pane, 900, 500);
//
//        imageView.setOnMouseClicked(e -> {
//            Stage stage = new Stage();
//            stage.setTitle("新窗口");
//            stage.setWidth(300);
//            stage.setHeight(200);
//            TextField field = new TextField();
//            field.setAlignment(Pos.CENTER);
//            field.setOnDragOver(event -> event.acceptTransferModes(TransferMode.ANY));
//            field.setOnDragDropped(event -> {
//                Dragboard dragboard = event.getDragboard();
//                if (dragboard.hasFiles()) {
//                    String path = dragboard.getFiles().get(0).getAbsolutePath();
//                    field.setText(path);
//                }
//            });
//            Button button = new Button("确认");
//            button.setOnMouseClicked(event -> {
//                qq = field.getText();
//                InputStream downloadImage1 = HttpUtil.doDownloadAvatar(qq);
//                assert downloadImage1 != null;
//                Image image1 = new Image(downloadImage1);
//                imageView.setImage(image1);
//                label.setText(qq);
//            });
//            AnchorPane b = new AnchorPane();
//            b.getChildren().add(new Label("新窗口"));
//            b.getChildren().add(field);
//            b.getChildren().add(button);
//            Scene scene1 = new Scene(b, 200, 100);
//            stage.setScene(scene1);
//            stage.initOwner(primaryStage);
//            stage.initModality(Modality.WINDOW_MODAL);
//            stage.show();
//        });
//
//        scene.setOnKeyReleased(event -> {
//            KeyCode code = event.getCode();
//            if (code.equals(KeyCode.UP)) {
//                label.setLayoutY(label.getLayoutY() - 5);
//            } else if (code.equals(KeyCode.DOWN)) {
//                label.setLayoutY(label.getLayoutY() + 5);
//            }
//        });

//        Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/demo.fxml")));

        FXMLLoader fxmlLoader = new FXMLLoader();
        System.out.println(Objects.requireNonNull(getClass().getResource("/fxml/demo.fxml")));
        fxmlLoader.setLocation(Objects.requireNonNull(getClass().getResource("/fxml/demo.fxml")));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 900, 500);

        //必须在场景new完后才能获取到
        MainController controller = fxmlLoader.getController();
        controller.initialize(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

}
