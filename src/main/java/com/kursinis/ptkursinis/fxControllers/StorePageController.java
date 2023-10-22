package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;

public class StorePageController implements PageController, Initializable {
    @FXML
    public TableView<Product> productsTableView;
    @FXML


    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private Product selectedProduct;
    private Product selectedCartProduct;

    private final ObservableList<Product> productsData = FXCollections.observableArrayList();
    private final ObservableList<Product> cartProductsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productsTableView.setItems(productsData);
        addTableSelectionListener();
    }

    private void addTableSelectionListener() {
        productsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(productsTableView.getSelectionModel().getSelectedItem() != null) {
                    selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
                }
            }
        });
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(this.entityManagerFactory);
        loadProducts();
    }

    private void loadProducts() {
        productsData.clear();
        productsData.addAll(customHib.getAllRecords(Product.class));
    }

}
