package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.InventoryService;
import tec.curso.prototype.services.ShowtimeService;

import java.io.IOException;

@Component
public class SmartInventoryController {
    @Autowired
    private InventoryService inventoryService; // Inyecta tu servicio de Spring

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
