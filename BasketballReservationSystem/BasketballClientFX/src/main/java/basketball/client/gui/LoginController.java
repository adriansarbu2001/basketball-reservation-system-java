package basketball.client.gui;

import basketball.model.User;
import basketball.services.IBasketballServices;
import basketball.services.ServiceException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    IBasketballServices basketballServices;

    @FXML
    public Button buttonLogin;
    @FXML
    public TextField textFieldUsername;
    @FXML
    public TextField textFieldPassword;

    public void setServer(IBasketballServices basketballServices) {
        this.basketballServices = basketballServices;
    }

    private void clearTextFields() {
        textFieldUsername.clear();
        textFieldPassword.clear();
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void onButtonLoginClick() {
        try {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("seller-view.fxml"));
                Parent root = fxmlLoader.load();
                SellerController ctrl = fxmlLoader.getController();
                User user = basketballServices.login(new User(textFieldUsername.getText(), textFieldPassword.getText()), ctrl);
                ctrl.setServer(this.basketballServices);
                ctrl.setUser(user);
                Scene scene = new Scene(root, 941, 465);
                Stage stage = new Stage();
                stage.setTitle("Seller");
                stage.setScene(scene);
                Stage primaryStage = (Stage) this.buttonLogin.getScene().getWindow();

                stage.setOnCloseRequest(event -> {
                    ctrl.logout();
                    primaryStage.show();
                });

                ctrl.setParent(primaryStage);
                clearTextFields();
                primaryStage.hide();
                stage.show();
            } catch (IOException e) {
                showNotification(e.getMessage(), Alert.AlertType.ERROR);
            }

        } catch (ServiceException e) {
            showNotification(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showNotification(String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setContentText(message);
        alert.showAndWait();
    }
}
