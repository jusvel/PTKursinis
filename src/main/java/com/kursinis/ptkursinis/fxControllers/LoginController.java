package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.LaunchGUI;
import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{
    private EntityManagerFactory entityManagerFactory;
    private CustomHib customHib;

    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button loginButton;
    @FXML
    public void verifyLogin() throws IOException {
        customHib = new CustomHib(entityManagerFactory);
        User u = customHib.getUserByCredentials(usernameField.getText(), passwordField.getText());

        if(u!=null){
            loadStore(u);
        } else {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.INFORMATION, "Login INFO", "Wrong data", "Please check credentials, no such user");
        }
    }

    private void loadEmployeeStore(User u) {
        System.out.println(u.getClass());
    }

    private void loadStore(User u) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource("view/MainWindowView.fxml"));
        Parent parent = fxmlLoader.load();
        MainWindowController storeController = fxmlLoader.getController();
        storeController.setData(entityManagerFactory, u);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Shop");
        JavaFxCustomUtils.setIcon(stage);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    @FXML
    public void goToRegistration(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource("view/registrationView.fxml"));
        Parent parent = fxmlLoader.load();
        RegistrationController registrationController = fxmlLoader.getController();
        Scene scene = new Scene(parent);
        registrationController.setData(scene, entityManagerFactory);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = Persistence.createEntityManagerFactory("kursinis-unit");
    }
}