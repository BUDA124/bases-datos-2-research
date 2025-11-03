package tec.curso.prototype;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"tec.curso.prototype.services", "tec.curso.prototype.controllers",
"tec.curso.prototype.services", "tec.curso.prototype.repositories", "tec.curso.prototype.store"})
public class ApplicationRunner {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}