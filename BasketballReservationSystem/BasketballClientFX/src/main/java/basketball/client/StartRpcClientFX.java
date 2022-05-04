package basketball.client;

import basketball.client.gui.LoginController;
import basketball.network.rpcprotocol.BasketballServicesRpcProxy;
import basketball.services.IBasketballServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClientFX extends Application {

    private static int defaultPort = 55556;
    private static String defaultServer = "localhost";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClientFX.class.getResourceAsStream("/basketballclient.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find basketballclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("basketball.server.host", defaultServer);
        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("basketball.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IBasketballServices server = new BasketballServicesRpcProxy(serverIP, serverPort);

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent root = loginLoader.load();
        LoginController ctrl = loginLoader.getController();
        ctrl.setServer(server);
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Basketball");
        stage.setScene(scene);
        stage.show();
    }
}
