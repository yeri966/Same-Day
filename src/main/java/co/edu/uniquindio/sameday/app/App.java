package co.edu.uniquindio.sameday.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/co/edu/uniquindio/sameday/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 340);

        stage.setTitle("SISTEMA DE LOGISTICA");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
