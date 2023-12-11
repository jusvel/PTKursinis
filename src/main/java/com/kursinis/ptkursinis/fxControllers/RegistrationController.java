package com.kursinis.ptkursinis.fxControllers;

import org.mindrot.jbcrypt.BCrypt;
import com.kursinis.ptkursinis.LaunchGUI;
import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.Customer;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    public TextField usernameField;
    public PasswordField passwordField;
    public TextField emailField;
    public TextField firstNameField;
    public PasswordField lastNameField;
    public TextField numberField;
    public TextField addressField;

    private EntityManagerFactory entityManagerFactory;
    private CustomHib customHib;

    private Scene scene;

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
        if (JavaFxCustomUtils.hasEmptyTextField(scene.getRoot())) {
            JavaFxCustomUtils.showError("Please fill in all fields");
        } else if(!customHib.isUsernameAvailable(usernameField.getText())){
            JavaFxCustomUtils.showError("Username is taken");
        } else if (!customHib.isEmailAvailable(emailField.getText())) {
            JavaFxCustomUtils.showError("Email is taken");
        } else {
            try{
                String hashedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());

                customHib.create(new Customer(usernameField.getText(),hashedPassword,emailField.getText(),
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
        customHib = new CustomHib(this.entityManagerFactory);
    }
}
