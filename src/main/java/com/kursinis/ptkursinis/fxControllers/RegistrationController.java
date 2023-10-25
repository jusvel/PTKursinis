package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.LaunchGUI;
import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.Customer;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    private EntityManagerFactory entityManagerFactory;
    private CustomHib customHib;
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField firstNameField;
    @FXML
    public PasswordField lastNameField;
    @FXML
    public TextField numberField;
    @FXML
    public TextField addressField;

    Scene scene;

    @FXML
    public void cancelRegistration() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource("view/loginView.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    @FXML
    public void signUp() throws IOException {
        if (JavaFxCustomUtils.isAnyTextFieldEmpty(scene)) {
            JavaFxCustomUtils.showError("Please fill in all fields");
        } else if(!customHib.isUsernameAvailable(usernameField.getText())){
            JavaFxCustomUtils.showError("Username is taken");
        } else if (!customHib.isEmailAvailable(emailField.getText())) {
            JavaFxCustomUtils.showError("Email is taken");
        } else {
            try{
                customHib.create(new Customer(usernameField.getText(),passwordField.getText(),emailField.getText(),
                        firstNameField.getText(),lastNameField.getText(), numberField.getText(),addressField.getText()));
            } catch (Exception e){
                e.printStackTrace();
            }
            JavaFxCustomUtils.showSuccess("Registration successful!");

            FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource("view/loginView.fxml"));
            Parent parent = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            scene = new Scene(parent);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Login");
            JavaFxCustomUtils.setIcon(stage);
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }
    }

    public void setData(Scene scene, EntityManagerFactory entityManagerFactory) {
        this.scene = scene;
        this.entityManagerFactory = entityManagerFactory;
        customHib = new CustomHib(entityManagerFactory);
    }
}
