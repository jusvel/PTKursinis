package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.CoolReview;
import com.kursinis.ptkursinis.model.Product;
import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CoolReviewsController implements PageController, Initializable {
    public TreeView<CoolReview> treeView;
    public TableView<Product> productTable;

    private ObservableList<Product> productsData = FXCollections.observableArrayList();

    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private Product selectedProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTable.setItems(productsData);
        addProductSelectionListener();
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

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(entityManagerFactory);
        loadProducts();


    }

    private void loadProducts() {
        productsData.clear();
        productsData.addAll(customHib.getAllRecords(Product.class));
    }

    private void loadComments() {
        List<CoolReview> reviews = customHib.getCoolReviewsByProductId(selectedProduct.getId());
        TreeItem<CoolReview> root = new TreeItem<>();
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        root.setExpanded(true);

        List<CoolReview> topLevelReviews = reviews.stream()
                .filter(review -> review.getParentId() == null)
                .collect(Collectors.toList());
        for (CoolReview review : topLevelReviews) {
            TreeItem<CoolReview> treeItem = new TreeItem<>(review);
            treeItem.setExpanded(true);
            root.getChildren().add(treeItem);
            addChildren(treeItem, reviews);
        }
    }

    private void addChildren(TreeItem<CoolReview> parent, List<CoolReview> reviews) {
        List<CoolReview> children = reviews.stream()
                .filter(review -> review.getParentId() != null && review.getParentId().equals(parent.getValue().getId()))
                .collect(Collectors.toList());

        for (CoolReview child : children) {
            TreeItem<CoolReview> treeItem = new TreeItem<>(child);
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
        CoolReview selectedReview = treeView.getSelectionModel().getSelectedItem().getValue();
        if(selectedReview.getUser().getId() == currentUser.getId()){
            CoolReview reviewToDelete = (CoolReview) customHib.getEntityById(CoolReview.class, selectedReview.getId());
            deleteChildComments(reviewToDelete);
            customHib.deleteCoolReview(reviewToDelete);
            selectedProduct.getCoolReviews().remove(reviewToDelete);
            loadComments();
            loadProducts();
        } else {
            JavaFxCustomUtils.showError("You can only delete your own comments");
        }
    }

    private void deleteChildComments(CoolReview parentReview) {
        List<CoolReview> allReviews = customHib.getCoolReviewsByProductId(parentReview.getProduct().getId());
        List<CoolReview> childReviews = allReviews.stream()
                .filter(review -> review.getParentId() != null && review.getParentId().equals(parentReview.getId()))
                .collect(Collectors.toList());

        for (CoolReview childReview : childReviews) {
            deleteChildComments(childReview);
            customHib.deleteCoolReview(childReview);
        }
    }

    public void reply(ActionEvent actionEvent) {
        if(treeView.getSelectionModel().getSelectedItem() == null) {
            JavaFxCustomUtils.showError("Please select a comment");
            return;
        }
        CoolReview selectedReview = treeView.getSelectionModel().getSelectedItem().getValue();
        CoolReview coolReview = new CoolReview();
        coolReview.setProduct(selectedProduct);
        coolReview.setUser(currentUser);
        coolReview.setParentId(selectedReview.getId());
        coolReview.setComment(JavaFxCustomUtils.showTextInputDialog("Comment", "Please enter your comment"));
        customHib.create(coolReview);
        selectedProduct.getCoolReviews().add(coolReview);
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
        CoolReview coolReview = new CoolReview();
        coolReview.setProduct(selectedProduct);
        coolReview.setUser(currentUser);
        coolReview.setRating(JavaFxCustomUtils.showRatingSlider());
        coolReview.setComment(JavaFxCustomUtils.showTextInputDialog("Comment", "Please enter your comment"));
        customHib.create(coolReview);
        selectedProduct.getCoolReviews().add(coolReview);
        loadProducts();
        loadComments();
    }

    private boolean selectedProductAlreadyReviewed() {
        for(CoolReview review : selectedProduct.getCoolReviews()){
            if(review.getUser().getUsername().equals(currentUser.getUsername())){
                return true;
            }
        }
        return false;
    }
}
