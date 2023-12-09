package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class StorePageController implements PageController, Initializable {
    @FXML
    public TableView<Product> productsTableView;
    public TableView<CartProduct> cartTableView;
    public TextField searchTextField;
    public Label priceLabel;

    private final ObservableList<Product> productsData = FXCollections.observableArrayList();
    private final ObservableList<CartProduct> cartProductsData = FXCollections.observableArrayList();

    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private Product selectedProduct;
    private CartProduct selectedCartProduct;
    private Cart cart;

    TableColumn<CartProduct, String> brandColumn = new TableColumn<>("Brand");
    TableColumn<CartProduct, String> nameColumn = new TableColumn<>("Name");
    TableColumn<CartProduct, Double> priceColumn = new TableColumn<>("Price");
    TableColumn<CartProduct, Integer> quantityColumn = new TableColumn<>("Quantity");
    TableColumn<Product, Double> avgRatingColumn = new TableColumn<>("Rating");



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productsTableView.setItems(productsData);
        cartTableView.setItems(cartProductsData);
        fixColumns();
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(this.entityManagerFactory);

        addTableSelectionListener(productsTableView);
        addCartTableSelectionListener(cartTableView);
        addTotalCounterToCart();

        cart = customHib.getCartByUser(currentUser);
        if(cart == null){
            cart = new Cart(currentUser, new ArrayList<>());
            customHib.create(cart);
        }

        loadProducts();
        loadCart();
    }

    private void loadCart() {
        cartProductsData.clear();
        cartProductsData.addAll(cart.getCartProducts());
    }

    private void loadProducts() {
        productsData.clear();
        productsData.addAll(customHib.getAllRecords(Product.class));
    }

    public void addToCart() {
        if(selectedProduct == null){
            JavaFxCustomUtils.showError("Please select a product");
            return;
        }

        cart = customHib.getCartByUser(currentUser);

        for(CartProduct cartProduct : cart.getCartProducts()){
            if(cartProduct.getProduct().getId() == selectedProduct.getId()){
                cartProduct.setQuantity(cartProduct.getQuantity() + 1);
                customHib.update(cart);
                loadCart();
                return;
            }
        }

        CartProduct cartProduct = new CartProduct(selectedProduct, 1, cart);
        cart.getCartProducts().add(cartProduct);
        customHib.update(cart);
        loadCart();
    }

    public void searchProduct() {
        productsData.clear();
        productsData.addAll(customHib.getProductSearch(Product.class,"name",searchTextField.getText()));
    }

    public void resetSearch() {
        productsTableView.getSelectionModel().clearSelection();
        searchTextField.setText("");
        loadProducts();
    }

    public void proceedToCheckout() {
        if (cartProductsData.isEmpty()){
            JavaFxCustomUtils.showError("Cart is empty");
        } else {
            String products = "";
            double totalPrice = 0.0;
            for (CartProduct cartProduct : cartProductsData) {
                products += cartProduct.getProduct().getBrand() + " " + cartProduct.getProduct().getName() + " x" + cartProduct.getQuantity() + "\n";
                totalPrice += cartProduct.getProduct().getPrice() * cartProduct.getQuantity();
            }
            for (CartProduct cartProduct : cartProductsData) {
                products += cartProduct.getProduct().getBrand() + " " + cartProduct.getProduct().getName() + " x" + cartProduct.getQuantity() + "\n";
            }
            String deliveryAddress = JavaFxCustomUtils.showTextInputDialog("Enter delivery address", "Enter delivery address");
            Order order = new Order(products, deliveryAddress, totalPrice, OrderStatus.PENDING, PaymentStatus.PENDING, "", LocalDate.now(), currentUser);
            customHib.create(order);

            cart = customHib.getCartByUser(currentUser);
            cart.getCartProducts().clear();
            customHib.update(cart);

            cartProductsData.clear();
            if(JavaFxCustomUtils.showConfirmation("Order has been placed successfully\n" +
                    "Order details:\n" +
                    "Products:\n" +
                    products +
                    "Delivery address: " + deliveryAddress +
                    "\nTotal price: $" + String.format("%.2f", totalPrice)+
                    "\n Press OK to pay now or Cancel to pay later")){
                order.setPaymentStatus(PaymentStatus.PAID);
                customHib.update(order);
                JavaFxCustomUtils.showSuccess("Order has been paid successfully");
            }

        }
    }

    public void removeProduct() {
        cart = customHib.getCartByUser(currentUser);
        if(selectedCartProduct != null){
            for (CartProduct cartProduct : cart.getCartProducts()) {
                if (cartProduct.getProduct().getId() == selectedCartProduct.getProduct().getId()) {
                    if (cartProduct.getQuantity() > 1) {
                        cartProduct.setQuantity(cartProduct.getQuantity() - 1);
                    } else {
                        cart.getCartProducts().remove(cartProduct);
                    }
                    customHib.update(cart);
                    loadCart();
                    return;
                }
            }
        } else{
            JavaFxCustomUtils.showError("Please select a product");
        }
    }

    public void removeAllProcuts() {
        cart = customHib.getCartByUser(currentUser);
        cart.getCartProducts().clear();
        customHib.update(cart);

        loadCart();
    }



    private void addTableSelectionListener(TableView<Product> tableView) {
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(tableView.getSelectionModel().getSelectedItem() != null) {
                    selectedProduct = tableView.getSelectionModel().getSelectedItem();
                }
            }
        });
    }
    private void addCartTableSelectionListener(TableView<CartProduct> tableView) {
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(tableView.getSelectionModel().getSelectedItem() != null) {
                    selectedCartProduct = tableView.getSelectionModel().getSelectedItem();
                }
            }
        });
    }

    private void addTotalCounterToCart() {
        cartProductsData.addListener((ListChangeListener<? super CartProduct>) c -> {
            double price = 0;
            for(CartProduct cartProduct : cartProductsData){
                price += cartProduct.getProduct().getPrice()* cartProduct.getQuantity();
            }
            priceLabel.setText("Total Price: $" + String.format("%.2f", price));
        });
    }

    private void fixColumns(){
        brandColumn.setCellValueFactory(cellData -> {
            CartProduct cartProduct = cellData.getValue();
            Product product = cartProduct.getProduct();
            if (product != null) {
                return new SimpleStringProperty(product.getBrand());
            } else {
                return new SimpleStringProperty("");
            }
        });

        nameColumn.setCellValueFactory(cellData -> {
            CartProduct cartProduct = cellData.getValue();
            Product product = cartProduct.getProduct();
            if (product != null) {
                return new SimpleStringProperty(product.getName());
            } else {
                return new SimpleStringProperty("");
            }
        });

        priceColumn.setCellValueFactory(cellData -> {
            CartProduct cartProduct = cellData.getValue();
            Product product = cartProduct.getProduct();
            if (product != null) {
                return new SimpleDoubleProperty(product.getPrice()).asObject();
            } else {
                return new SimpleDoubleProperty(0.0).asObject();
            }
        });

        quantityColumn.setCellValueFactory(cellData -> {
            CartProduct cartProduct = cellData.getValue();
            return new SimpleIntegerProperty(cartProduct.getQuantity()).asObject();
        });

        avgRatingColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            double avgRating = calculateAverageRating(product);
            return new SimpleDoubleProperty(avgRating).asObject();
        });
        productsTableView.getColumns().add(avgRatingColumn);
        cartTableView.getColumns().add(brandColumn);
        cartTableView.getColumns().add(nameColumn);
        cartTableView.getColumns().add(priceColumn);
        cartTableView.getColumns().add(quantityColumn);
    }

    private double calculateAverageRating(Product product) {
        double averageRating = 0;
        for(Review review : product.getReviews()){
            averageRating+= review.getRating();
        }
        averageRating/=product.getReviews().size();
        return averageRating;
    }
}
