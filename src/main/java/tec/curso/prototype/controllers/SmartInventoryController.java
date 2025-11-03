package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;

import java.io.IOException;

@Component
public class SmartInventoryController {

    @FXML
    public void initialize() {
        // actualizarDatos();
    }

    @FXML
    private void registerProductSale() {
        // TODO
    }

    private void actualizarDatos() {
        // TODO
    }

    @FXML
    private void action() throws IOException {
        JavaFxApplication.changeScene("SingUp.fxml");
    }
}
