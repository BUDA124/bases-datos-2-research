package tec.curso.prototype;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationRunner {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}