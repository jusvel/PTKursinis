package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.LaunchGUI;
import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.helpers.StringHelpers;
import com.kursinis.ptkursinis.model.Employee;
import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {
    public Button storeButton;
    public Button discussionButton;
    public Button itemsButton;
    public Button warehousesButton;
    public Button customersButton;
    public Button logOutButton;
    public Button reviewsButton;
    public Button myOrdersButton;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;

    @FXML
    public Button employeesButton;
    @FXML
    public VBox vbox;
    @FXML
    BorderPane borderPane;

    @FXML
    private void loadPage(ActionEvent event) {
        String pageName = StringHelpers.camelCaseConverter(((Button) event.getSource()).getText());
        String fxmlPath = "view/"+ pageName + "Page.fxml";
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource(fxmlPath));;
            root = fxmlLoader.load();
            PageController pageController = fxmlLoader.getController();
            pageController.setData(entityManagerFactory,currentUser);

        } catch (Exception e){
            e.printStackTrace();
        }
        borderPane.setCenter(root);
    }

    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        if(currentUser.getClass().getSimpleName().equals("Customer")){
            vbox.getChildren().removeAll(vbox.getChildren());
            vbox.getChildren().add(storeButton);
            vbox.getChildren().add(myOrdersButton);
            vbox.getChildren().add(reviewsButton);
            vbox.getChildren().add(discussionButton);
            vbox.getChildren().add(logOutButton);
        } else if(currentUser.getClass().getSimpleName().equals("Employee") && !((Employee) currentUser).getIsAdmin()){
            vbox.getChildren().remove(employeesButton);
        }
    }

    public void logOut() throws IOException {
        currentUser = null;
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchGUI.class.getResource("view/loginView.fxml"));
        Parent parent = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) vbox.getScene().getWindow();
        stage.setTitle("Login");
        JavaFxCustomUtils.setIcon(stage);
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}
