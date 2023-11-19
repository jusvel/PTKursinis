package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class ReviewsPageController implements PageController, Initializable {
    public TableView<Product> productTable;
    public Label reviewLabel;

    private final ObservableList<Product> productData = FXCollections.observableArrayList();
    public ListView<Review> reviewsList;
    private Product selectedProduct;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(entityManagerFactory);
        loadProducts();
    }

    private void loadProducts() {
        productData.clear();
        productData.addAll(customHib.getAllRecords(Product.class));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(productData);
        addSelectionListener();
        TableColumn<Product, Double> avgRatingColumn = new TableColumn<>("Average Rating");
        avgRatingColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            double avgRating = calculateAverageRating(product);
            return new SimpleDoubleProperty(avgRating).asObject();
        });
        productTable.getColumns().add(avgRatingColumn);
    }

    private void addSelectionListener() {
        productTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                selectedProduct = newValue;
                if(selectedProduct!=null){
                    reviewLabel.setText("Avarage rating: " + calculateAverageRating(selectedProduct));
                    reviewsList.setItems(FXCollections.observableArrayList(selectedProduct.getReviews()));
                }
            }
        });
    }

    private double calculateAverageRating(Product product) {
        double averageRating = 0;
        for(Review review : product.getReviews()){
            averageRating+= review.getRating();
        }
        averageRating/=product.getReviews().size();
        return averageRating;
    }


    public void rate(ActionEvent actionEvent) {
        int selectedIndex = productTable.getSelectionModel().getSelectedIndex();
        if (selectedProduct == null) {
            JavaFxCustomUtils.showError("Please select a product");
            return;
        }
        if (selectedProductAlreadyReviewed()) {
            JavaFxCustomUtils.showError("You have already reviewed this product");
            return;
        }
        Review review = new Review();
        review.setProduct(selectedProduct);
        review.setUser(currentUser);
        review.setRating(JavaFxCustomUtils.showRatingDialog());
        review.setComment(JavaFxCustomUtils.showTextInputDialog("Comment", "Please enter your comment"));
        customHib.create(review);
        selectedProduct.getReviews().add(review);
        loadProducts();
        productTable.getSelectionModel().select(selectedIndex);
        reviewLabel.setText("Average rating: " + calculateAverageRating(productTable.getItems().get(selectedIndex)));
    }



    private boolean selectedProductAlreadyReviewed() {
        for(Review review : selectedProduct.getReviews()){
            if(review.getUser().getUsername().equals(currentUser.getUsername())){
                return true;
            }
        }
        return false;
    }
}
