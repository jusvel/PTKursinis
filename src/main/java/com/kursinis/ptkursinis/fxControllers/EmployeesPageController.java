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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EmployeesPageController implements PageController, Initializable {
    @FXML
    public TableView<Employee> employeeTable;
    public TextField usernameField;
    public TextField passwordField;
    public TextField emailField;
    public TextField firstnameField;
    public TextField lastnameField;
    public TextField numberField;
    public TextField employeeIdField;
    public CheckBox isAdminCheckBox;
    public DatePicker employmentDateDatePicker;
    @FXML
    public ChoiceBox columnSelectionBox;
    public VBox fieldVBox;
    public TextField searchField;

    CustomHib customHib;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;

    private Employee selectedEmployee;

    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        employeeTable.setItems(employeeData);
        addSelectionListener();
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(entityManagerFactory);
        loadEmployees();
        setupSelectionBox();
    }

    private void setupSelectionBox() {
        employeeTable.getColumns().forEach(employeeTableColumn -> {
            columnSelectionBox.getItems().addAll(employeeTableColumn.getText());
        });
        columnSelectionBox.getItems().remove("Employment Date");
        columnSelectionBox.getItems().remove("Is Admin");
        columnSelectionBox.getItems().remove("Type");
    }

    private void loadEmployees() {
        employeeData.clear();
        employeeData.addAll(customHib.getAllRecords(Employee.class));
    }

    public void deselectEmployee() {
        employeeTable.getSelectionModel().clearSelection();
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        firstnameField.setText("");
        lastnameField.setText("");
        numberField.setText("");
        employeeIdField.setText("");
        isAdminCheckBox.setSelected(false);
        employmentDateDatePicker.setValue(null);
        selectedEmployee = null;
    }

    public void addEmployee() {
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
        Employee employee = new Employee(usernameField.getText(), passwordField.getText(), emailField.getText(), firstnameField.getText(), lastnameField.getText(), numberField.getText(), Integer.parseInt(employeeIdField.getText()), employmentDateDatePicker.getValue(), isAdminCheckBox.isSelected());
        customHib.create(employee);
        employeeData.add(employee);
        selectedEmployee = employee;
        loadEmployees();
        JavaFxCustomUtils.showSuccess("Employee added successfully");
        employeeTable.getSelectionModel().select(employee);
    }

    public void deleteSelectedEmployee() {
        if(selectedEmployee == null){
            JavaFxCustomUtils.showError("Please select an employee");
            return;
        }
        customHib.delete(selectedEmployee.getClass(), selectedEmployee.getId());
        employeeData.remove(selectedEmployee);
        selectedEmployee = null;
        deselectEmployee();
        loadEmployees();
        JavaFxCustomUtils.showSuccess("Employee deleted successfully");
    }

    public void updateSelectedEmployee() {
        if(selectedEmployee == null){
            JavaFxCustomUtils.showError("Please select an employee");
            return;
        }
        if(JavaFxCustomUtils.isAnyTextFieldEmpty(fieldVBox)){
            JavaFxCustomUtils.showError("Please fill in all fields");
            return;
        }
        selectedEmployee.setUsername(usernameField.getText());
        selectedEmployee.setPassword(passwordField.getText());
        selectedEmployee.setEmail(emailField.getText());
        selectedEmployee.setFirstName(firstnameField.getText());
        selectedEmployee.setLastName(lastnameField.getText());
        selectedEmployee.setPhoneNumber(numberField.getText());
        selectedEmployee.setEmployeeId(Integer.parseInt(employeeIdField.getText()));
        selectedEmployee.setIsAdmin(isAdminCheckBox.isSelected());
        selectedEmployee.setEmploymentDate(employmentDateDatePicker.getValue());
        customHib.update(selectedEmployee);

        loadEmployees();
        JavaFxCustomUtils.showSuccess("Employee updated successfully");
    }

    public void findEmployee(){
        if(columnSelectionBox.getValue() == null && !searchField.getText().isEmpty()){
            JavaFxCustomUtils.showError("Please select a column");
            return;
        } else if(searchField.getText().isEmpty()){
            loadEmployees();
            return;
        }
        employeeData.clear();
        employeeData.addAll(customHib.getEntitiesSearch(Employee.class, columnSelectionBox.getValue().toString(), searchField.getText()));
    }

    public void resetSearch() {
        columnSelectionBox.getSelectionModel().clearSelection();
        searchField.setText("");
        loadEmployees();
    }

    private void addSelectionListener() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(employeeTable.getSelectionModel().getSelectedItem() != null) {
                    selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
                    boolean isAdmin = (selectedEmployee.getIsAdmin()!=null) ? selectedEmployee.getIsAdmin() : false;

                    usernameField.setText(selectedEmployee.getUsername());
                    passwordField.setText(selectedEmployee.getPassword());
                    emailField.setText(selectedEmployee.getEmail());
                    firstnameField.setText(selectedEmployee.getFirstName());
                    lastnameField.setText(selectedEmployee.getLastName());
                    numberField.setText(selectedEmployee.getPhoneNumber());
                    employeeIdField.setText(String.valueOf(selectedEmployee.getEmployeeId()));
                    isAdminCheckBox.setSelected(isAdmin);
                    employmentDateDatePicker.setValue(selectedEmployee.getEmploymentDate());
                    System.out.println(selectedEmployee.toString());
                }
            }
        });
    }
}
