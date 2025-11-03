package tec.curso.prototype.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.SalesAreaService;

import java.io.IOException;

@Component
public class SalesArea {

    @Autowired
    private SalesAreaService salesAreaService;

    @FXML
    public void initialize() {
        // Se llama al cargar la vista.
        // Carga los datos iniciales desde el dashboardService y actualiza los labels y el gráfico.
        // actualizarDatos();
    }

    /**
     * This method is now designed to be called by a button's onAction event.
     * It simulates registering a sale and then shows a confirmation notification.
     *
     * @param event The ActionEvent triggered by the UI component (e.g., a button click).
     */
    @FXML
    private void registerSale(ActionEvent event) {
        // TODO: Add the business logic here to save the sale data.

        // Example:
        // String product = productTextField.getText();
        // double amount = Double.parseDouble(amountTextField.getText());
        // financialService.saveSale(product, amount);

        Notifications.create()
                .title("Sale Registered")
                .text("The new sale has been added successfully.")
                .position(Pos.BOTTOM_LEFT)
                .hideAfter(Duration.seconds(5)) // Auto-hide after 5 seconds
                .owner(getStageFromEvent(event)) // Attach to the correct window
                .showConfirm(); // Display with a confirmation icon
    }

    private void actualizarDatos() {
        // TODO
    }

    @FXML
    private void action() throws IOException {
        JavaFxApplication.changeScene("SingUp.fxml");
    }

    private Stage getStageFromEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}