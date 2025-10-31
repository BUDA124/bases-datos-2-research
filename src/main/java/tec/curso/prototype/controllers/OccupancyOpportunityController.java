package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.MovieSalesService;
import tec.curso.prototype.services.ShowtimeService;

import java.io.IOException;

@Component
public class OccupancyOpportunityController {
    @Autowired
    private ShowtimeService showtimeService; // Inyecta tu servicio de Spring

    @FXML
    public void initialize() {
        // actualizarDatos();
    }

    @FXML
    private void registerTicketSale() {
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
