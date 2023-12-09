package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.Customer;
import com.kursinis.ptkursinis.model.Employee;
import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomersPageController implements Initializable, PageController{
    @FXML
    public TableView<Customer> customerTable;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField firstnameField;
    @FXML
    public TextField lastnameField;
    @FXML
    public TextField numberField;
    @FXML
    public TextField addressField;
    @FXML
    public VBox fieldVBox;
    @FXML
    public TextField searchField;
    @FXML
    public ChoiceBox columnSelectionBox;
    public Button addCustomerButton;
    public Button deleteSelectedCustomerButton;
    public Button updateSelectedCustomerButton;

    CustomHib customHib;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;

    private Customer selectedCustomer;

    private ObservableList<Customer> customersData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerTable.setItems(customersData);
        addSelectionListener();

    }

    private void loadCustomers() {
        customersData.clear();
        customersData.addAll(customHib.getAllRecords(Customer.class));
    }

    private void addSelectionListener() {
        customerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(customerTable.getSelectionModel().getSelectedItem() != null) {
                    selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
                    usernameField.setText(selectedCustomer.getUsername());
                    passwordField.setText(selectedCustomer.getPassword());
                    emailField.setText(selectedCustomer.getEmail());
                    firstnameField.setText(selectedCustomer.getFirstName());
                    lastnameField.setText(selectedCustomer.getLastName());
                    numberField.setText(selectedCustomer.getPhoneNumber());
                    addressField.setText(selectedCustomer.getAddress());
                }
            }
        });
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(entityManagerFactory);
        loadCustomers();
        customerTable.getColumns().forEach(customerTableColumn -> {
            columnSelectionBox.getItems().add(customerTableColumn.getText());
        });
        columnSelectionBox.getItems().remove("Type");
        if(currentUser.getType().equals("E")){
            if(!((Employee) currentUser).getIsAdmin()) {
                usernameField.setDisable(true);
            passwordField.setDisable(true);
            emailField.setDisable(true);
            firstnameField.setDisable(true);
            lastnameField.setDisable(true);
            numberField.setDisable(true);
            addressField.setDisable(true);
            addCustomerButton.setDisable(true);
            deleteSelectedCustomerButton.setDisable(true);
            updateSelectedCustomerButton.setDisable(true);
            }
        }
    }

    public void deselectCustomer() {
        customerTable.getSelectionModel().clearSelection();
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        firstnameField.setText("");
        lastnameField.setText("");
        numberField.setText("");
        addressField.setText("");
        selectedCustomer = null;
    }

    public void addCustomer() {
        if(JavaFxCustomUtils.isAnyTextFieldEmpty(fieldVBox)){
            JavaFxCustomUtils.showError("Please fill in all fields");
            return;
        } else if(!customHib.isUsernameAvailable(usernameField.getText())){
            JavaFxCustomUtils.showError("Username is already taken");
            return;
        } else if(!customHib.isEmailAvailable(emailField.getText())){
            JavaFxCustomUtils.showError("Email is already taken");
            return;
        }
        String password = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
        Customer customer = new Customer(usernameField.getText(), password, emailField.getText(), firstnameField.getText(), lastnameField.getText(), numberField.getText(), addressField.getText());
        customHib.create(customer);
        customersData.add(customer);
        selectedCustomer = customer;
        loadCustomers();
        JavaFxCustomUtils.showSuccess("Customer added successfully");
        customerTable.getSelectionModel().select(customer);
    }

    public void deleteSelectedCustomer() {
        //cascade pakeist databaseje
        if(selectedCustomer == null){
            JavaFxCustomUtils.showError("Please select a customer");
            return;
        }
        customHib.delete(selectedCustomer.getClass(), selectedCustomer.getId());
        customersData.remove(selectedCustomer);
        selectedCustomer = null;
        deselectCustomer();
        loadCustomers();
        JavaFxCustomUtils.showSuccess("Customer deleted successfully");
    }

    public void updateSelectedCustomer() {
        if(selectedCustomer == null){
            JavaFxCustomUtils.showError("Please select a customer");
            return;
        }
        if(JavaFxCustomUtils.isAnyTextFieldEmpty(fieldVBox)){
            JavaFxCustomUtils.showError("Please fill in all fields");
            return;
        }
        String password = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
        selectedCustomer.setUsername(usernameField.getText());
        selectedCustomer.setPassword(password);
        selectedCustomer.setEmail(emailField.getText());
        selectedCustomer.setFirstName(firstnameField.getText());
        selectedCustomer.setLastName(lastnameField.getText());
        selectedCustomer.setPhoneNumber(numberField.getText());
        selectedCustomer.setAddress(addressField.getText());
        customHib.update(selectedCustomer);
        loadCustomers();
        JavaFxCustomUtils.showSuccess("Customer updated successfully");
    }

    public void findCustomer(){
        if(columnSelectionBox.getValue() == null && !searchField.getText().isEmpty()){
            JavaFxCustomUtils.showError("Please select a column");
            return;
        } else if(searchField.getText().isEmpty()){
            loadCustomers();
            return;
        }
        customersData.clear();
        customersData.addAll(customHib.getEntitiesSearch(Customer.class, columnSelectionBox.getValue().toString(), searchField.getText()));
    }

    public void resetSearch() {
        columnSelectionBox.getSelectionModel().clearSelection();
        searchField.setText("");
        loadCustomers();
    }
}
