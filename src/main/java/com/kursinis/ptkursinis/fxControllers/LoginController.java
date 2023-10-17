package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.LaunchGUI;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{


    public TextField usernameField;
    public PasswordField passwordField;

    public void verifyLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource("view/storeView.fxml"));
        Parent parent = fxmlLoader.load();
        StoreController storeController = fxmlLoader.getController();

        Scene scene = new Scene(parent);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Shop");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}