package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;

import java.io.IOException;

@Component
public class SingUpController {

    @FXML
    public void otherAction() throws IOException {
        JavaFxApplication.changeScene("LogIn.fxml");
    }
}
