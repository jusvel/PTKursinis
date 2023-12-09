package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.kursinis.ptkursinis.model.OrderStatus.*;

public class OrdersPageController implements PageController, Initializable {
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public TextField usernameTextField;
    public TextField responsibleEmloyeeTextField;

    public TableView<Order> OrderTableView;
    private final ObservableList<Order> ordersData = javafx.collections.FXCollections.observableArrayList();
    public PieChart orderStatusChart;
    public LineChart chartOfRevenue;

    TableColumn<Order, String> productsColumn = new TableColumn<>("Products");
    TableColumn<Order, String> deliveryAddressColumn = new TableColumn<>("Delivery Address");
    TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
    TableColumn<Order, String> paymentStatusColumn = new TableColumn<>("Payment Status");
    TableColumn<Order, String> dateCreatedColumn = new TableColumn<>("Date Created");
    TableColumn<Order, String> totalPriceColumn = new TableColumn<>("Total Price");
    TableColumn<Order, String> customerColumn = new TableColumn<>("Customer");
    TableColumn<Order, String> responsibleEmployeeColumn = new TableColumn<>("Responsible Employee");
    TableColumn<Order, Void> actionsColumn = new TableColumn<>("Actions");

    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;

    private ArrayList<String> employeeUsernames = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        OrderTableView.setItems(ordersData);
    }
    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(this.entityManagerFactory);
        loadEmployeeUsernames();
        loadOrders();
        fixColumns();
        updateCharts();
    }


    private void loadEmployeeUsernames() {
        ArrayList<Employee> employees = new ArrayList<>(customHib.getAllRecords(Employee.class));
        for (Employee employee : employees) {
            employeeUsernames.add(employee.getUsername());
        }
    }

    private void loadOrders() {
        ordersData.clear();
        ordersData.addAll(customHib.getAllRecords(Order.class));
    }

    private void updateChartOfRevenue() {
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Revenue");

        Map<LocalDate, Double> revenueMap = new TreeMap<>();
        for(Order order : ordersData){
            revenueMap.put(order.getDateCreated(), revenueMap.getOrDefault(order.getDateCreated(), 0.0) + order.getTotalPrice());
        }

        for (Map.Entry<LocalDate, Double> entry : revenueMap.entrySet()){
            XYChart.Data<String, Double> data = new XYChart.Data<>(entry.getKey().toString(), entry.getValue());
            series.getData().add(data);

            data.setNode(new StackPane());
            Label label = new Label(String.format("%.2f", entry.getValue()));
            label.setStyle("-fx-font-size: 10px;");
            StackPane.setAlignment(label, Pos.TOP_CENTER);
            ((StackPane) data.getNode()).getChildren().add(label);
        }

        chartOfRevenue.getData().add(series);
    }

    private void updatePieChart() {
        Map<OrderStatus,Integer> statusCountMap = new HashMap<>();
        for(Order order : ordersData){
            statusCountMap.put(order.getStatus(), statusCountMap.getOrDefault(order.getStatus(), 0) + 1);
        }
        for (Map.Entry<OrderStatus, Integer> entry : statusCountMap.entrySet()){
            PieChart.Data slice = new PieChart.Data(entry.getKey().toString(), entry.getValue());
            orderStatusChart.getData().add(slice);
        }
        int totalCount = ordersData.size();


        for (PieChart.Data data : orderStatusChart.getData()) {
            double percentage = data.getPieValue() / totalCount * 100;
            data.nameProperty().bind(
                    Bindings.concat(
                            data.getName(), ": ", data.pieValueProperty(), " (",
                            String.format("%.1f", percentage), "%)"
                    )
            );
        }
    }


    private void fixColumns() {
        productsColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducts()));
        deliveryAddressColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDeliveryAddress()));
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString()));
        paymentStatusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPaymentStatus().toString()));
        dateCreatedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateCreated().toString()));
        totalPriceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getTotalPrice())));
        customerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser().getUsername()));

        addResponsibleEmployeeColumn();
        addActionColumn();
        setColumnWidths();
        addColumnsAndAlert();
    }

    private void addResponsibleEmployeeColumn() {
        responsibleEmployeeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getResponsibleEmployee()));
        responsibleEmployeeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(employeeUsernames)));
        responsibleEmployeeColumn.setOnEditCommit((TableColumn.CellEditEvent<Order, String> event) -> {
            TablePosition<Order, String> pos = event.getTablePosition();
            String newResponsibleEmployee = event.getNewValue();
            int row = pos.getRow();
            Order order = event.getTableView().getItems().get(row);
            order.setResponsibleEmployee(newResponsibleEmployee);
            order.setStatus(IN_PROGRESS);
            customHib.update(order);
            loadOrders();
        });
        responsibleEmployeeColumn.setEditable(true);
    }

    private void addActionColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button completeButton = new Button("Complete");
            private final Button deleteButton = new Button("Delete");
            private final Button chatButton = new Button("Chat");
            private final HBox pane = new HBox(completeButton, deleteButton, chatButton);
            {
                completeButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    if(order.getStatus().equals(PENDING)){
                        JavaFxCustomUtils.showError("You can't complete pending order, you must select an employee first");
                        return;
                    }
                    if(order.getStatus().equals(CANCELLED)){
                        JavaFxCustomUtils.showError("You can't complete cancelled order");
                        return;
                    }
                    if(order.getStatus().equals(COMPLETED)){
                        JavaFxCustomUtils.showError("You can't complete completed order");
                        return;
                    }
                    if(order.getPaymentStatus().equals(PaymentStatus.PENDING)){
                        JavaFxCustomUtils.showError("You can't complete order with pending payment");
                        return;
                    }
                    order.setStatus(COMPLETED);
                    customHib.update(order);
                    loadOrders();
                });

                deleteButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    if(order.getStatus().equals(COMPLETED)){
                        JavaFxCustomUtils.showError("You can't delete completed order");
                        return;
                    }
                    if(order.getStatus().equals(IN_PROGRESS)){
                        JavaFxCustomUtils.showError("You can't delete in progress order");
                        return;
                    }
                    customHib.delete(Order.class, order.getId());
                    loadOrders();
                });

                chatButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showChat(order.getUser(), currentUser, order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void addColumnsAndAlert() {
        OrderTableView.setEditable(true);
        OrderTableView.getColumns().addAll(customerColumn, productsColumn, totalPriceColumn, deliveryAddressColumn, statusColumn, paymentStatusColumn, dateCreatedColumn, responsibleEmployeeColumn, actionsColumn);
        OrderTableView.setRowFactory(tv -> new TableRow<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getResponsibleEmployee().equals("") && item.getDateCreated().isBefore(LocalDate.now().minusDays(1)) && item.getStatus().equals(PENDING)) {
                    setStyle("-fx-background-color: #e24e4e;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void setColumnWidths() {
        productsColumn.setPrefWidth(200);
        deliveryAddressColumn.setPrefWidth(150);
        totalPriceColumn.setPrefWidth(80);
        dateCreatedColumn.setPrefWidth(80);
        statusColumn.setPrefWidth(80);
        paymentStatusColumn.setPrefWidth(80);
        responsibleEmployeeColumn.setPrefWidth(100);
        customerColumn.setPrefWidth(90);
        actionsColumn.setPrefWidth(210);
    }

    public void search(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String responsibleEmployee = responsibleEmloyeeTextField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        if(username.equals("") && responsibleEmployee.equals("") && startDate == null && endDate == null){
            loadOrders();
            return;
        }
        ordersData.clear();
        ordersData.addAll(customHib.getOrdersByFilters(username, responsibleEmployee, startDate, endDate));
        updateCharts();
    }

    private void updateCharts() {
        orderStatusChart.getData().clear();
        chartOfRevenue.getData().clear();
        updatePieChart();
        updateChartOfRevenue();
    }

    public void resetSearch(ActionEvent actionEvent) {
        usernameTextField.setText("");
        responsibleEmloyeeTextField.setText("");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        loadOrders();
        updateCharts();
    }

    public void showChat(User orderer, User employee, Order order) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chat with " + orderer.getUsername());

        ListView<Message> listView = new ListView<>();
        List<Message> messages = customHib.getMessagesOfOrder(order);
        listView.getItems().addAll(messages);

        TextField textField = new TextField();
        textField.setOnAction(event -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                Message message = sendMessage(employee, text, order);
                listView.getItems().add(message);
                textField.clear();
            }
        });
        VBox vbox = new VBox(listView, textField);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private Message sendMessage(User user, String text, Order order) {
        Message message = new Message(user, null, order, text, LocalDateTime.now());
        customHib.create(message);
        return message;
    }
}
