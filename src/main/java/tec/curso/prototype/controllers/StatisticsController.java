package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.MovieSalesService;

import java.io.IOException;

@Component
public class StatisticsController {
    @Autowired
    private MovieSalesService movieSalesService; // Inyecta tu servicio de Spring

    @FXML
    public void initialize() {
        // actualizarDatos();
    }

    @FXML
    private void registerMovieSale() {
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
