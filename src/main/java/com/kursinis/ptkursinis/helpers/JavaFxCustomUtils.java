package com.kursinis.ptkursinis.helpers;

import com.kursinis.ptkursinis.model.User;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

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

    public static boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        return alert.getResult().getText().equals("OK");
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

    public static double showRatingSlider(){
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Rate");
        dialog.setHeaderText("Please enter your rating (1-5)");

        Slider slider = new Slider();
        slider.setMin(1);
        slider.setMax(5);
        slider.setValue(3);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);

        Label sliderValue = new Label(Double.toString(slider.getValue()));
        slider.valueProperty().addListener((observable, oldValue, newValue) -> sliderValue.setText(String.format("%.1f", newValue)));

        VBox vbox = new VBox(slider, sliderValue);
        dialog.getDialogPane().setContent(vbox);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(new Callback<ButtonType, Double>() {
            @Override
            public Double call(ButtonType buttonType) {
                if (buttonType == okButtonType) {
                    return slider.getValue();
                }
                return null;
            }
        });
        return dialog.showAndWait().orElse(3.0);
    }

    public static void showChat(User user, User currentUser) {
        System.out.println("Showing chat");
    }
}
