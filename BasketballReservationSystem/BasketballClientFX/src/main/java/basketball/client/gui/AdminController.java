package basketball.client.gui;

import basketball.services.IBasketballServices;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AdminController {

    IBasketballServices basketballServices;
    String user;
    Stage parent;

    public void setServer(IBasketballServices basketballServices) {
        this.basketballServices = basketballServices;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setParent(Stage parent) {
        this.parent = parent;
    }

    @FXML
    public void initialize() {

    }

    private void showNotification(String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setContentText(message);
        alert.showAndWait();
    }
}
