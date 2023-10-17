package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.LaunchGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class StoreController {
    @FXML
    BorderPane borderPane;

    @FXML
    private void loadPage(ActionEvent event) {
        String fxmlPath = "view/"+((Button) event.getSource()).getText().toLowerCase() + "Page.fxml";
        System.out.println(fxmlPath);

        Parent root = null;
        try {
            root = FXMLLoader.load(LaunchGUI.class.getResource(fxmlPath));
        } catch (Exception e){
            e.printStackTrace();
        }
        borderPane.setCenter(root);
    }
}
