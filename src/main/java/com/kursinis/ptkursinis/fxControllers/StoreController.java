package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.LaunchGUI;
import com.kursinis.ptkursinis.model.Employee;
import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class StoreController {
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
        String fxmlPath = "view/"+((Button) event.getSource()).getText().toLowerCase() + "Page.fxml";
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
        } else if(currentUser.getClass().getSimpleName().equals("Employee") && ((Employee) currentUser).getIsAdmin()){
            //TODO: add employee page
        } else if(currentUser.getClass().getSimpleName().equals("Employee") && !((Employee) currentUser).getIsAdmin()){
            vbox.getChildren().remove(employeesButton);
        }
    }

}
