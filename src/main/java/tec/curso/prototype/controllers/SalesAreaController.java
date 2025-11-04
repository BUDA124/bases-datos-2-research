package tec.curso.prototype.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tec.curso.prototype.JavaFxApplication;
import tec.curso.prototype.services.SalesAreaService;

import java.io.IOException;

@Component
public class SalesAreaController {

    @Autowired
    private SalesAreaService salesAreaService; // Inyecta tu servicio de Spring

    @FXML
    private VBox ticketsContainer;

    @FXML
    private VBox foodContainer;

    @FXML
    public void initialize() {

        // Ejemplo: listas de productos
        String[] tickets = {"Star Wars Ticket", "Home Alone Ticket", "The Godfather Ticket"};
        String[] foodItems = {"Popcorn", "Soda", "Nachos"};

        // Cargar los ítems dinámicamente
        loadItems(ticketsContainer, tickets);
        loadItems(foodContainer, foodItems);

        // Se llama al cargar la vista.
        // Carga los datos iniciales desde el dashboardService y actualiza los labels y el gráfico.
        // actualizarDatos();
    }

    private void loadItems(VBox container, String[] items) {
        container.setFillWidth(false);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);


        for (String item : items) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 5; -fx-background-color: #ffffff; -fx-background-radius: 5;");

            row.setPrefWidth(400);
            row.setMaxWidth(400);

            Label name = new Label(item);
            name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Spinner<Integer> spinner = new Spinner<>(0, 10, 0);

            row.getChildren().addAll(name, spinner);
            container.getChildren().add(row);
        }
    }
    /**
     * This method is now designed to be called by a button's onAction event.
     * It simulates registering a sale and then shows a confirmation notification.
     *
     * @param event The ActionEvent triggered by the UI component (e.g., a button click).
     */
    @FXML
    private void registerSale(ActionEvent event) {

        boolean anySelected = hasSelectedItems(ticketsContainer) || hasSelectedItems(foodContainer);
        if (!anySelected) {
            Notifications.create()
                    .title("No Sale")
                    .text("Please select at least one item before registering a sale.")
                    .position(Pos.BOTTOM_LEFT)
                    .hideAfter(Duration.seconds(3))
                    .owner(getStageFromEvent(event))
                    .showWarning();
            return;
        }

        // TODO: Add the business logic here to save the sale data.
        processSale(ticketsContainer);
        processSale(foodContainer);
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
        clearSpinners(ticketsContainer);
        clearSpinners(foodContainer);
    }

    private void processSale(VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                Label name = (Label) row.getChildren().get(0);
                Spinner<Integer> spinner = (Spinner<Integer>) row.getChildren().get(1);

                int quantity = spinner.getValue();
                if (quantity > 0) {
                    System.out.println("Sold " + quantity + " of " + name.getText());
                    // Aquí puedes llamar a tu FinancialService:
                    // financialService.registerSale(name.getText(), quantity);
                }
            }
        }
    }

    private void clearSpinners(VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                Spinner<Integer> spinner = (Spinner<Integer>) row.getChildren().get(1);
                spinner.getValueFactory().setValue(0);
            }
        }
    }

    private boolean hasSelectedItems(VBox container) {
        for (Node node : container.getChildren()) {
            if (node instanceof HBox row) {
                Spinner<Integer> spinner = (Spinner<Integer>) row.getChildren().get(1);
                if (spinner.getValue() > 0) return true;
            }
        }
        return false;
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