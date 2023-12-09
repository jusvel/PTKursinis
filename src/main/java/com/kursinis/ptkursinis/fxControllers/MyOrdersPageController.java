package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.kursinis.ptkursinis.model.OrderStatus.*;

public class MyOrdersPageController implements PageController, Initializable {
    public TableView<Order> ordersTableView;
    private final ObservableList<Order> ordersData = javafx.collections.FXCollections.observableArrayList();
    public Button payForOrderButton;
    public Button cancelOrderButton;

    TableColumn<Order, String> productsColumn = new TableColumn<>("Products");
    TableColumn<Order, String> deliveryAddressColumn = new TableColumn<>("Delivery Address");
    TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
    TableColumn<Order, String> paymentStatusColumn = new TableColumn<>("Payment Status");
    TableColumn<Order, String> dateCreatedColumn = new TableColumn<>("Date Created");
    TableColumn<Order, String> totalPriceColumn = new TableColumn<>("Total Price");
    TableColumn<Order, Void> actionsColumn = new TableColumn<>("Actions");

    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private Order selectedOrder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelOrderButton.setDisable(true);
        payForOrderButton.setDisable(true);
        ordersTableView.setItems(ordersData);

        fixColumns();
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(this.entityManagerFactory);
        loadOrders();
        addTableSelectionListener(ordersTableView);
    }

    private void addTableSelectionListener(TableView<Order> ordersTableView) {
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedOrder = newValue;
            if(selectedOrder == null){
                return;
            }
            if(selectedOrder.getPaymentStatus().equals(PaymentStatus.PAID)){
                payForOrderButton.setDisable(true);
            } else {
                payForOrderButton.setDisable(false);
            }
            if(selectedOrder.getStatus().equals(OrderStatus.CANCELLED)){
                cancelOrderButton.setDisable(true);
            } else {
                cancelOrderButton.setDisable(false);
            }
        });
    }

    public void payForOrder(ActionEvent actionEvent) {
        if(!JavaFxCustomUtils.showConfirmation("Are you sure you want to pay for this order?")){
            return;
        }
        selectedOrder.setPaymentStatus(PaymentStatus.PAID);
        customHib.update(selectedOrder);
        selectedOrder = null;
        payForOrderButton.setDisable(true);
        cancelOrderButton.setDisable(true);
        loadOrders();
    }

    public void cancelOrder(ActionEvent actionEvent) {
        if(!JavaFxCustomUtils.showConfirmation("Are you sure you want to cancel this order?")){
            return;
        }
        selectedOrder.setStatus(OrderStatus.CANCELLED);
        customHib.update(selectedOrder);
        selectedOrder = null;
        cancelOrderButton.setDisable(true);
        payForOrderButton.setDisable(true);
        loadOrders();
    }

    private void loadOrders() {
        ordersData.clear();
        ordersData.addAll(customHib.getOrdersByUser(currentUser));
    }

    private void fixColumns() {
        productsColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducts()));
        deliveryAddressColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDeliveryAddress()));
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        paymentStatusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPaymentStatus().toString()));
        dateCreatedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateCreated().toString()));
        totalPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getTotalPrice())));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelButton = new Button("Cancel");
            private final Button payButton = new Button("Pay");
            private final Button chatButton = new Button("Chat");
            private final HBox pane = new HBox(cancelButton, payButton, chatButton);

            {
                cancelButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    if(order.getStatus().equals(CANCELLED)){
                        JavaFxCustomUtils.showError("This order is already cancelled");
                        return;
                    }
                    if(!JavaFxCustomUtils.showConfirmation("Are you sure you want to cancel this order?")){
                        return;
                    }
                    order.setStatus(CANCELLED);
                    customHib.update(order);
                    loadOrders();
                });

                payButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    if(order.getPaymentStatus().equals(PaymentStatus.PAID)){
                        JavaFxCustomUtils.showError("This order is already paid");
                        return;
                    }
                    if(!JavaFxCustomUtils.showConfirmation("Are you sure you want to pay for this order?")){
                        return;
                    }
                    order.setPaymentStatus(PaymentStatus.PAID);
                    customHib.update(order);
                    loadOrders();
                });

                chatButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    JavaFxCustomUtils.showChat(order.getUser(), currentUser);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        productsColumn.setPrefWidth(300);
        deliveryAddressColumn.setPrefWidth(150);
        totalPriceColumn.setPrefWidth(100);
        dateCreatedColumn.setPrefWidth(100);
        statusColumn.setPrefWidth(100);
        paymentStatusColumn.setPrefWidth(100);
        actionsColumn.setPrefWidth(250);
        ordersTableView.getColumns().add(productsColumn);
        ordersTableView.getColumns().add(totalPriceColumn);
        ordersTableView.getColumns().add(deliveryAddressColumn);
        ordersTableView.getColumns().add(dateCreatedColumn);
        ordersTableView.getColumns().add(statusColumn);
        ordersTableView.getColumns().add(paymentStatusColumn);
        ordersTableView.getColumns().add(actionsColumn);
    }
}
