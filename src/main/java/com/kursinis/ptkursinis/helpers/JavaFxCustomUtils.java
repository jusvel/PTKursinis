package com.kursinis.ptkursinis.helpers;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class JavaFxCustomUtils {
    //TODO change this to something more personal
    public static void generateAlert(Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static double showRatingDialog() {
        String ratingString = JavaFxCustomUtils.showTextInputDialog("Rate", "Please enter your rating (0-5)");
        double rating = 0;
        try{
            rating = Double.parseDouble(ratingString);
        } catch (NumberFormatException e){
            JavaFxCustomUtils.showError("Please enter a valid number");
            return showRatingDialog();
        }
        if(rating < 0 || rating > 5){
            JavaFxCustomUtils.showError("Please enter a number between 0 and 5");
            return showRatingDialog();
        }
        return rating;
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String showTextInputDialog(String title, String header){
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(header);
        return textInputDialog.showAndWait().orElse("");
    }

    public static boolean isAnyTextFieldEmpty(Scene scene) {
        for(TextField textField : getAllTextFields(scene)){
            if(textField.getText().isEmpty()){
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyTextFieldEmpty(VBox vbox) {
        for(TextField textField : getAllTextFields(vbox)){
            if(textField.getText().isEmpty()){
                return true;
            }
        }
        return false;
    }

    public static List<TextField> getAllTextFields(Scene scene) {
        List<TextField> textFields = new ArrayList<>();
        for (Node node : scene.getRoot().getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                textFields.add((TextField) node);
            }
        }
        return textFields;
    }
    public static List<TextField> getAllTextFields(VBox vbox) {
        List<TextField> textFields = new ArrayList<>();
        for (Node node : vbox.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                textFields.add((TextField) node);
            }
        }
        return textFields;
    }

    public static void setIcon(Stage stage) {
        stage.getIcons().add(new Image("shopping-basket.png"));
    }



}
