package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.helpers.JavaFxCustomUtils;
import com.kursinis.ptkursinis.hibernateControllers.CustomHib;
import com.kursinis.ptkursinis.model.*;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class ItemPageController implements PageController, Initializable {
    @FXML
    public TableView<Product> productsTableView;
    @FXML
    public ListView warehouseListView;
    public ChoiceBox productTypeChoiceBox;
    public ChoiceBox columnSelectChoiceBox;
    public TextField searchTextField;
    public TextField brandTextField;
    public TextField nameTextField;
    public TextField priceTextField;
    public TextField osTextField;
    public TextField inchesTextField;
    public TextField resolutionTextField;
    public TextField batteryTextField;
    public TextField ramTextField;
    public TextField weightTextField;
    public TextField storageTextField;
    public ChoiceBox warehouseSelectChoiceBox;
    public TextField lengthTextField;
    public TableColumn<Product, Integer> storageColumn;
    public TableColumn<Product, String> osColumn;
    public TableColumn<Product, Double> inchesColumn;
    public TableColumn<Product, String> resolutionColumn;
    public TableColumn<Product, Integer> batteryColumn;
    public TableColumn<Product, Integer> ramColumn;
    public TableColumn<Product, Integer> weightColumn;

    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private List<Warehouse> allWarehouses;
    Set<String> productTypes = new HashSet<>();
    private Product selectedProduct;

    private final ObservableList<Product> productsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productsTableView.setItems(productsData);
        addTableSelectionListener();
        addListSelectionListener();
        setupColumns();

    }


    private void addListSelectionListener() {
        warehouseListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(warehouseListView.getSelectionModel().getSelectedItem() != null) {
                    Warehouse selectedWarehouse = allWarehouses.get(warehouseListView.getSelectionModel().getSelectedIndex());
                    productsData.clear();
                    productsData.addAll(selectedWarehouse.getProductsInWarehouse());
                }
            }
        });
    }

    private void addTableSelectionListener() {
        productsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(productsTableView.getSelectionModel().getSelectedItem() != null) {
                    selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
                    String type = selectedProduct.getClass().getAnnotation(DiscriminatorValue.class).value();
                    productTypeChoiceBox.getSelectionModel().select(type);
                    brandTextField.setText(selectedProduct.getBrand());
                    nameTextField.setText(selectedProduct.getName());
                    priceTextField.setText(String.valueOf(selectedProduct.getPrice()));
                    if(type.equals("Phone")){
                        osTextField.setText(((Phone) selectedProduct).getOs());
                        inchesTextField.setText(String.valueOf(((Phone) selectedProduct).getInches()));
                        resolutionTextField.setText(((Phone) selectedProduct).getResolution());
                        batteryTextField.setText(String.valueOf(((Phone) selectedProduct).getBattery()));
                        ramTextField.setText(String.valueOf(((Phone) selectedProduct).getRam()));
                        weightTextField.setText(String.valueOf(((Phone) selectedProduct).getWeight()));
                        storageTextField.setText(String.valueOf(((Phone) selectedProduct).getStorage()));
                        lengthTextField.setText("");
                    } else if(type.equals("Charger")){
                        osTextField.setText("");
                        inchesTextField.setText("");
                        resolutionTextField.setText("");
                        batteryTextField.setText("");
                        ramTextField.setText("");
                        weightTextField.setText("");
                        storageTextField.setText("");
                        lengthTextField.setText(String.valueOf(((Charger) selectedProduct).getLength()));
                    }
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
        loadItems();
        productTypeChoiceBoxSetup();
        productsTableView.getColumns().forEach(column -> {
            columnSelectChoiceBox.getItems().add(column.getText());
        });
        allWarehouses.forEach(warehouse -> warehouseSelectChoiceBox.getItems().add(warehouse.getName()));
    }

    private void productTypeChoiceBoxSetup() {
        customHib.getAllRecords(Product.class).forEach(product -> {
            productTypes.add(product.getClass().getAnnotation(DiscriminatorValue.class).value());
        });
        productTypeChoiceBox.getItems().addAll(productTypes);

        productTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Phone")){
                    osTextField.setDisable(false);
                    inchesTextField.setDisable(false);
                    resolutionTextField.setDisable(false);
                    batteryTextField.setDisable(false);
                    ramTextField.setDisable(false);
                    weightTextField.setDisable(false);
                    storageTextField.setDisable(false);
                    lengthTextField.setDisable(true);
                } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Charger")){
                    osTextField.setDisable(true);
                    inchesTextField.setDisable(true);
                    resolutionTextField.setDisable(true);
                    batteryTextField.setDisable(true);
                    ramTextField.setDisable(true);
                    weightTextField.setDisable(true);
                    storageTextField.setDisable(true);
                    lengthTextField.setDisable(false);
                }
            }
        });
    }

    private void loadItems() {
        productsData.clear();
        productsData.addAll(customHib.getAllRecords(Product.class));
    }

    private void loadWarehouses() {
        warehouseListView.getItems().clear();
        allWarehouses = customHib.getAllRecords(Warehouse.class);
        allWarehouses.forEach(warehouse -> warehouseListView.getItems().add(warehouse.getName()));
    }

    public void viewAllProducts(){
        productsData.clear();
        productsData.addAll(customHib.getAllRecords(Product.class));
    }


    public void deselect() {
        productsTableView.getSelectionModel().clearSelection();
        brandTextField.setText("");
        nameTextField.setText("");
        priceTextField.setText("");
        osTextField.setText("");
        inchesTextField.setText("");
        resolutionTextField.setText("");
        batteryTextField.setText("");
        ramTextField.setText("");
        weightTextField.setText("");
        storageTextField.setText("");
        lengthTextField.setText("");
        selectedProduct = null;
    }

    public void addProduct() {
        Warehouse selectedWarehouse = allWarehouses.get(warehouseSelectChoiceBox.getSelectionModel().getSelectedIndex());
        Warehouse warehouse = (Warehouse) customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());

        try{
        if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Phone")){
            customHib.create(new Phone(nameTextField.getText(),Double.parseDouble(priceTextField.getText()), brandTextField.getText(), warehouse, osTextField.getText(),Double.parseDouble(inchesTextField.getText()), resolutionTextField.getText(), Integer.parseInt(batteryTextField.getText()), Integer.parseInt(ramTextField.getText()), Integer.parseInt(weightTextField.getText()), Integer.parseInt(storageTextField.getText())));
        } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Charger")){
            customHib.create(new Charger(nameTextField.getText(), Double.parseDouble(priceTextField.getText()), brandTextField.getText(),warehouse, Integer.parseInt(lengthTextField.getText())));
        }
        } catch (Exception e) {
            JavaFxCustomUtils.showError("Please fill out all fields!");
        }
        loadItems();
        loadWarehouses();

        JavaFxCustomUtils.showSuccess("Product added successfully");
    }

    public void deleteProduct() {
        customHib.deleteFromWarehouse(selectedProduct);
        deselect();

        loadItems();
        loadWarehouses();
        JavaFxCustomUtils.showSuccess("Product deleted successfully");
    }

    public void updateProduct() {
        if(selectedProduct == null){
            JavaFxCustomUtils.showError("Please select a customer");
            return;
        }
        try{
            selectedProduct.setBrand(brandTextField.getText());
            selectedProduct.setName(nameTextField.getText());
            selectedProduct.setPrice(Double.parseDouble(priceTextField.getText()));
            if(selectedProduct.getClass().getAnnotation(DiscriminatorValue.class).value().equals("Phone")){
                ((Phone) selectedProduct).setOs(osTextField.getText());
                ((Phone) selectedProduct).setInches(Double.parseDouble(inchesTextField.getText()));
                ((Phone) selectedProduct).setResolution(resolutionTextField.getText());
                ((Phone) selectedProduct).setBattery(Integer.parseInt(batteryTextField.getText()));
                ((Phone) selectedProduct).setRam(Integer.parseInt(ramTextField.getText()));
                ((Phone) selectedProduct).setWeight(Integer.parseInt(weightTextField.getText()));
                ((Phone) selectedProduct).setStorage(Integer.parseInt(storageTextField.getText()));
            } else if(selectedProduct.getClass().getAnnotation(DiscriminatorValue.class).value().equals("Charger")){
                ((Charger) selectedProduct).setLength(Integer.parseInt(lengthTextField.getText()));
            }
            selectedProduct.setWarehouse(allWarehouses.get(warehouseSelectChoiceBox.getSelectionModel().getSelectedIndex()));
            customHib.update(selectedProduct);
        } catch (Exception e) {
            JavaFxCustomUtils.showError("Please fill out all fields!");
        }

        loadItems();
        loadWarehouses();
        JavaFxCustomUtils.showSuccess("Product updated successfully");
    }

    public void searchProduct() {
    }

    public void reset() {
    }

    private void setupColumns(){
        storageColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getStorage()).asObject();
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
        osColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleStringProperty(((Phone) cellData.getValue()).getOs());
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
        inchesColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleDoubleProperty(((Phone) cellData.getValue()).getInches()).asObject();
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
        resolutionColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleStringProperty(((Phone) cellData.getValue()).getResolution());
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
        batteryColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getBattery()).asObject();
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
        ramColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getRam()).asObject();
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
        weightColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getWeight()).asObject();
            } else if(cellData.getValue() instanceof Charger){
                return  null;
            }
            return null;
        });
    }
}
