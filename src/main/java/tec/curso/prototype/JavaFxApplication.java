package tec.curso.prototype;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private static Stage primaryStage;
    private static JavaFxApplication instance;

    @Override
    public void init() {
        instance = this;
        applicationContext = new SpringApplicationBuilder(ApplicationRunner.class).run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationRunner.class.getResource("Welcome.fxml"));
        fxmlLoader.setControllerFactory(applicationContext::getBean);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    public static void changeScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(JavaFxApplication.class.getResource(fxml));

        loader.setControllerFactory(instance.applicationContext::getBean);

        Parent pane = loader.load();
        primaryStage.getScene().setRoot(pane);
    }
}