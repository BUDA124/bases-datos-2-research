package tec.curso.prototype.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.FinancialService;

import java.io.IOException;

@Component
public class FinancialPulseController {

    @Autowired
    private FinancialService financialService; // Inyecta tu servicio de Spring

    @FXML
    public void initialize() {
        // Se llama al cargar la vista.
        // Carga los datos iniciales desde el dashboardService y actualiza los labels y el gráfico.
        // actualizarDatos();
    }

    @FXML
    private void registerSale() {
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