package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.Product;
import com.kursinis.ptkursinis.model.Review;
import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReviewsPageController implements PageController, Initializable {
    public TreeView<Review> treeView;
    public TableView<Product> productTable;

    private ObservableList<Product> productsData = FXCollections.observableArrayList();
    TableColumn<Product, Double> avgRatingColumn = new TableColumn<>("Rating");
    private Product selectedProduct;


    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(productsData);
        addProductSelectionListener();
        addAvarageRatingColumn();
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(this.entityManagerFactory);
        loadProducts();
    }

    private void addAvarageRatingColumn() {
        avgRatingColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            double avgRating = calculateAverageRating(product);
            return new SimpleDoubleProperty(avgRating).asObject();
        });
        productTable.getColumns().add(avgRatingColumn);
    }

    private void addProductSelectionListener() {
        productTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                if(newValue == null) return;
                selectedProduct = newValue;
                loadComments();
            }
        });
    }

    private void loadProducts() {
        productsData.clear();
        productsData.addAll(customHib.getAllRecords(Product.class));
    }

    private void loadComments() {
        List<Review> reviews = customHib.getReviewsByProductId(selectedProduct.getId());
        TreeItem<Review> root = new TreeItem<>();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        root.setExpanded(true);

        List<Review> topLevelReviews = reviews.stream()
                .filter(review -> review.getParentId() == null)
                .collect(Collectors.toList());
        for (Review review : topLevelReviews) {
            TreeItem<Review> treeItem = new TreeItem<>(review);
            treeItem.setExpanded(true);
            root.getChildren().add(treeItem);
            addChildren(treeItem, reviews);
        }
    }

    private void addChildren(TreeItem<Review> parent, List<Review> reviews) {
        List<Review> children = reviews.stream()
                .filter(review -> review.getParentId() != null && review.getParentId().equals(parent.getValue().getId()))
                .collect(Collectors.toList());

        for (Review child : children) {
            TreeItem<Review> treeItem = new TreeItem<>(child);
            treeItem.setExpanded(true);
            parent.getChildren().add(treeItem);
            addChildren(treeItem, reviews);
        }
    }


    public void deleteComment(ActionEvent actionEvent) {
        if(treeView.getSelectionModel().getSelectedItem() == null) {
            JavaFxCustomUtils.showError("Please select a comment");
            return;
        }
        Review selectedReview = treeView.getSelectionModel().getSelectedItem().getValue();
        if(selectedReview.getUser().getId() == currentUser.getId()){
            Review reviewToDelete = (Review) customHib.getEntityById(Review.class, selectedReview.getId());
            deleteChildComments(reviewToDelete);
            customHib.deleteReview(reviewToDelete);
            selectedProduct.getReviews().remove(reviewToDelete);
            loadComments();
            loadProducts();
        } else {
            JavaFxCustomUtils.showError("You can only delete your own comments");
        }
    }

    private void deleteChildComments(Review parentReview) {
        List<Review> allReviews = customHib.getReviewsByProductId(parentReview.getProduct().getId());
        List<Review> childReviews = allReviews.stream()
                .filter(review -> review.getParentId() != null && review.getParentId().equals(parentReview.getId()))
                .collect(Collectors.toList());

        for (Review childReview : childReviews) {
            deleteChildComments(childReview);
            customHib.deleteReview(childReview);
        }
    }

    public void reply(ActionEvent actionEvent) {
        if(treeView.getSelectionModel().getSelectedItem() == null) {
            JavaFxCustomUtils.showError("Please select a comment");
            return;
        }
        Review selectedReview = treeView.getSelectionModel().getSelectedItem().getValue();
        Review review = new Review();
        review.setProduct(selectedProduct);
        review.setUser(currentUser);
        review.setParentId(selectedReview.getId());
        review.setComment(JavaFxCustomUtils.showTextInputDialog("Comment", "Please enter your comment"));
        customHib.create(review);
        selectedProduct.getReviews().add(review);
        loadProducts();
        loadComments();
    }

    public void reviewSelectedProduct(){
        if(selectedProduct == null){
            JavaFxCustomUtils.showError("Please select a product");
            return;
        }
        if(selectedProductAlreadyReviewed()){
            JavaFxCustomUtils.showError("You have already reviewed this product");
            return;
        }
        Review review = new Review();
        review.setProduct(selectedProduct);
        review.setUser(currentUser);
        review.setReview(true);
        review.setRating(JavaFxCustomUtils.showRatingSlider());
        review.setComment(JavaFxCustomUtils.showTextInputDialog("Comment", "Please enter your comment"));
        customHib.create(review);
        selectedProduct.getReviews().add(review);
        loadProducts();
        loadComments();
    }

    private boolean selectedProductAlreadyReviewed() {
        for(Review review : selectedProduct.getReviews()){
            if(review.getUser().getUsername().equals(currentUser.getUsername())){
                return true;
            }
        }
        return false;
    }

    private double calculateAverageRating(Product product) {
        double averageRating = 0;
        int count = 0;
        for(Review review : product.getReviews()){
            if(review.isReview()){
                averageRating+= review.getRating();
                count++;
            }
        }
        averageRating/=count;
        return averageRating;
    }
}
