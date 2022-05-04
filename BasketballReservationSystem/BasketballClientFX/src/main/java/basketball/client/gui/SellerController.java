package basketball.client.gui;

import basketball.model.User;
import basketball.model.Match;
import basketball.model.validator.ValidatorException;
import basketball.services.IBasketballObserver;
import basketball.services.IBasketballServices;
import basketball.services.ServiceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Collection;

public class SellerController implements IBasketballObserver {

    IBasketballServices basketballServices;
    User user;
    Stage parent;

    ObservableList<Match> modelMatch = FXCollections.observableArrayList();
    ObservableList<Match> modelMatchOrdered = FXCollections.observableArrayList();

    @FXML
    TableView<Match> tableViewMatches;
    @FXML
    public TableColumn<Match, Long> tableColumnMatchId;
    @FXML
    public TableColumn<Match, String> tableColumnName;
    @FXML
    public TableColumn<Match, Float> tableColumnTicketPrice;
    @FXML
    public TableColumn<Match, Integer> tableColumnNoAvailableSeats;
    @FXML
    public TableColumn<Match, String> tableColumnAvailableSoldOut;

    @FXML
    TableView<Match> tableViewMatchesOrdered;
    @FXML
    public TableColumn<Match, Integer> tableColumnMatchIdOrdered;
    @FXML
    public TableColumn<Match, String> tableColumnNameOrdered;
    @FXML
    public TableColumn<Match, Float> tableColumnTicketPriceOrdered;
    @FXML
    public TableColumn<Match, Integer> tableColumnNoAvailableSeatsOrdered;

    @FXML
    public TextField textFieldClientName;
    @FXML
    public TextField textFieldNoSeats;
    @FXML
    public TextField textFieldMatchId;
    @FXML
    public Button buttonSell;
    @FXML
    public Button buttonLogOut;

    private void updateModels() {
        try {
            modelMatch.setAll((Collection<? extends Match>) this.basketballServices.findAll());
            modelMatchOrdered.setAll((Collection<? extends Match>) this.basketballServices.availableMatchesDescending());
        } catch (ServiceException e) {
            showNotification(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void setServer(IBasketballServices basketballServices) {
        this.basketballServices = basketballServices;
        updateModels();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setParent(Stage parent) {
        this.parent = parent;
    }

    private void initializeTableViewMatches() {
        tableColumnMatchId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tableColumnTicketPrice.setCellValueFactory(new PropertyValueFactory<>("TicketPrice"));
        tableColumnNoAvailableSeats.setCellValueFactory(new PropertyValueFactory<>("NoAvailableSeats"));
        tableColumnAvailableSoldOut.setCellValueFactory(new PropertyValueFactory<>("StatusMessage"));
        tableViewMatches.setItems(modelMatch);
        tableViewMatches.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                textFieldMatchId.setText(String.valueOf(newSelection.getId()));
            }
        });
    }

    private void initializeTableViewMatchesOrdered() {
        tableColumnMatchIdOrdered.setCellValueFactory(new PropertyValueFactory<>("Id"));
        tableColumnNameOrdered.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tableColumnTicketPriceOrdered.setCellValueFactory(new PropertyValueFactory<>("TicketPrice"));
        tableColumnNoAvailableSeatsOrdered.setCellValueFactory(new PropertyValueFactory<>("NoAvailableSeats"));
        tableViewMatchesOrdered.setItems(modelMatchOrdered);
        tableViewMatchesOrdered.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                textFieldMatchId.setText(String.valueOf(newSelection.getId()));
            }
        });
    }

    @FXML
    public void initialize() {
        initializeTableViewMatches();
        initializeTableViewMatchesOrdered();
    }

    private void showNotification(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onButtonSellClick() {
        try {
            basketballServices.saveTicket(textFieldClientName.getText(), textFieldNoSeats.getText(), textFieldMatchId.getText());
        } catch (ValidatorException | ServiceException e) {
            showNotification(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void onButtonLogOutClick() {
        logout();
        ((Stage) buttonLogOut.getScene().getWindow()).close();
        this.parent.show();
    }

    void logout() {
        try {
            basketballServices.logout(user, this);
        } catch (ServiceException e) {
            System.out.println("Logout error " + e);
        }
    }

    @Override
    public void ticketSold() {
        Platform.runLater(()->{
            updateModels();
            System.out.println("Updated tables");
        });
    }
}
