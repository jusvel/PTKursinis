package com.kursinis.ptkursinis.helpers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class JavaFxCustomUtils {
    public static void generateAlert(Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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

    public static boolean hasEmptyTextField(Parent parent) {
        for (TextField textField : getAllTextFields(parent)) {
            if (textField.getText().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static List<TextField> getAllTextFields(Parent parent) {
        List<TextField> textFields = new ArrayList<>();
        for (Node node : parent.getChildrenUnmodifiable()) {
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
}
