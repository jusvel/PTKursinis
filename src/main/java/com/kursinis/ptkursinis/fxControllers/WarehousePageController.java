package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.Customer;
import com.kursinis.ptkursinis.model.Product;
import com.kursinis.ptkursinis.model.User;
import com.kursinis.ptkursinis.model.Warehouse;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class WarehousePageController implements PageController, Initializable {
    @FXML
    public TableView<Warehouse> warehousesTableView;
    public TextField nameTextField;
    public TextField addressTextField;
    public ChoiceBox selectionChoiceBox;
    public TextField searchTextField;
    public VBox fieldVBox;
    private ObservableList<Warehouse> warehouseData = FXCollections.observableArrayList();
    Warehouse selectedWarehouse;

    CustomHib customHib;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warehousesTableView.setItems(warehouseData);
        addTableSelectionListener();
    }
    private void addTableSelectionListener() {
        warehousesTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(warehousesTableView.getSelectionModel().getSelectedItem() != null) {
                    selectedWarehouse = warehouseData.get(warehousesTableView.getSelectionModel().getSelectedIndex());
                    nameTextField.setText(selectedWarehouse.getName());
                    addressTextField.setText(selectedWarehouse.getAddress());
                }
            }
        });
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(entityManagerFactory);
        loadWarehouses();
        warehousesTableView.getColumns().forEach(warehousesTableViewColumn -> {
            selectionChoiceBox.getItems().add(warehousesTableViewColumn.getText());
        });
    }


    private void loadWarehouses() {
        warehouseData.clear();
        warehouseData.addAll(customHib.getAllRecords(Warehouse.class));
    }

    public void deselect() {
        warehousesTableView.getSelectionModel().clearSelection();
        nameTextField.setText("");
        addressTextField.setText("");
        selectedWarehouse = null;
    }

    public void addWarehouse() {
        if(JavaFxCustomUtils.isAnyTextFieldEmpty(fieldVBox)) {
            JavaFxCustomUtils.showError("Please fill all fields");
            return;
        }
        Warehouse warehouse = new Warehouse(nameTextField.getText(), addressTextField.getText());
        customHib.create(warehouse);
        loadWarehouses();
        deselect();
    }

    public void deleteWarehouse() {
        //TODO send products of a warehouse to another warehouse?
        if(selectedWarehouse == null) {
            JavaFxCustomUtils.showError("Please select a warehouse");
            return;
        }
        customHib.delete(Warehouse.class, selectedWarehouse.getId());
        loadWarehouses();
        deselect();
    }

    public void updateWarehouse() {
        if(selectedWarehouse == null) {
            JavaFxCustomUtils.showError("Please select a warehouse");
            return;
        }
        selectedWarehouse.setName(nameTextField.getText());
        selectedWarehouse.setAddress(addressTextField.getText());
        customHib.update(selectedWarehouse);
        loadWarehouses();
    }

    public void searchWarehouse() {
        if(selectionChoiceBox.getValue() == null && !searchTextField.getText().isEmpty()){
            JavaFxCustomUtils.showError("Please select a column");
            return;
        } else if(searchTextField.getText().isEmpty()){
            loadWarehouses();
            return;
        }
        warehouseData.clear();
        warehouseData.addAll(customHib.getEntitiesSearch(Warehouse.class, selectionChoiceBox.getValue().toString(), searchTextField.getText()));
    }

    public void resetSearch() {
        selectionChoiceBox.getSelectionModel().clearSelection();
        searchTextField.setText("");
        loadWarehouses();
    }
}
