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

public class ItemPageController implements PageController, Initializable {
    @FXML
    public TableView<Product> productsTableView;
    @FXML
    public ListView warehouseListView;

    public ChoiceBox productTypeChoiceBox;
    public ChoiceBox columnSelectChoiceBox;
    public ChoiceBox warehouseSelectChoiceBox;

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
    public TextField connectionTypeTextField;
    public TextField diameterTextField;

    public TableColumn<Product, Integer> storageColumn;
    public TableColumn<Product, String> osColumn;
    public TableColumn<Product, Double> inchesColumn;
    public TableColumn<Product, String> resolutionColumn;
    public TableColumn<Product, Integer> batteryColumn;
    public TableColumn<Product, Integer> ramColumn;
    public TableColumn<Product, Integer> weightColumn;
    public TableColumn<Product, Integer> diameterColumn;
    public TableColumn<Product, String> connectionTypeColumn;

    Set<String> productTypes = new HashSet<>();
    private List<Warehouse> allWarehouses;

    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;
    private Product selectedProduct;

    private final ObservableList<Product> productsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productsTableView.setItems(productsData);
        addTableSelectionListener();
        addWarehouseListSelectionListener();
        setupColumns();
    }

    @Override
    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        customHib = new CustomHib(this.entityManagerFactory);
        loadWarehouses();
        loadProducts();
        productTypeChoiceBoxSetup();
        productsTableView.getColumns().forEach(column -> {
            columnSelectChoiceBox.getItems().add(column.getText());
        });
        allWarehouses.forEach(warehouse -> warehouseSelectChoiceBox.getItems().add(warehouse.getName()));
    }

    private void loadProducts() {
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
        diameterTextField.setText("");
        connectionTypeTextField.setText("");
        selectedProduct = null;
    }

    public void addProduct() {
        if(warehouseSelectChoiceBox.getSelectionModel().getSelectedItem() == null){
            JavaFxCustomUtils.showError("Please select a warehouse");
            return;
        }
        Warehouse selectedWarehouse = allWarehouses.get(warehouseSelectChoiceBox.getSelectionModel().getSelectedIndex());
        Warehouse warehouse = (Warehouse) customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
        String name = nameTextField.getText();
        String brand = brandTextField.getText();
        double price = Double.parseDouble(priceTextField.getText());

        try{
            if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Phone")){
                customHib.create(new Phone(name,price,brand, warehouse, osTextField.getText(),Double.parseDouble(inchesTextField.getText()), resolutionTextField.getText(), Integer.parseInt(batteryTextField.getText()), Integer.parseInt(ramTextField.getText()), Integer.parseInt(weightTextField.getText()), Integer.parseInt(storageTextField.getText())));
            } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Smartwatch")){
                customHib.create(new Smartwatch(name,price,brand,warehouse, Integer.parseInt(diameterTextField.getText())));
            } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Headphones")){
                customHib.create(new Headphones(name,price,brand,warehouse, connectionTypeTextField.getText()));
            } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Other")){
                customHib.create(new Other(name,price,brand,warehouse));
            } else {
                JavaFxCustomUtils.showError("Please select a product type");
                return;
            }
        } catch (Exception e) {
            JavaFxCustomUtils.showError("Please fill out all fields!");
        }
        loadProducts();
        loadWarehouses();
        JavaFxCustomUtils.showSuccess("Product added successfully");
    }

    public void deleteProduct() {
        customHib.deleteFromWarehouse(selectedProduct);
        deselect();
        loadProducts();
        loadWarehouses();
        JavaFxCustomUtils.showSuccess("Product deleted successfully");
    }

    public void updateProduct() {
        if(selectedProduct == null){
            JavaFxCustomUtils.showError("Please select a product");
            System.out.println("Here");
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
            } else if(selectedProduct.getClass().getAnnotation(DiscriminatorValue.class).value().equals("Smartwatch")){
                ((Smartwatch) selectedProduct).setDiameter(Integer.parseInt(diameterTextField.getText()));
            } else if(selectedProduct.getClass().getAnnotation(DiscriminatorValue.class).value().equals("Headphones")){
                ((Headphones) selectedProduct).setConnectionType(connectionTypeTextField.getText());
            } else if(selectedProduct.getClass().getAnnotation(DiscriminatorValue.class).value().equals("Other")){

            }
            selectedProduct.setWarehouse(allWarehouses.get(warehouseSelectChoiceBox.getSelectionModel().getSelectedIndex()));
            customHib.update(selectedProduct);
        } catch (Exception e) {
            JavaFxCustomUtils.showError("Please fill out all fields!");
            e.printStackTrace();
        }

        loadProducts();
        loadWarehouses();
        JavaFxCustomUtils.showSuccess("Product updated successfully");
    }



    public void searchProduct() {
        if(columnSelectChoiceBox.getValue() == null && !searchTextField.getText().isEmpty()){
            JavaFxCustomUtils.showError("Please select a column");
            return;
        } else if(searchTextField.getText().isEmpty()){
            loadProducts();
            return;
        }
        productsData.clear();

        String column = columnSelectChoiceBox.getValue().toString();
        String value = searchTextField.getText();
        System.out.println(column + " " + value);

        switch (column.toLowerCase()) {
            case "id", "name", "price", "brand","type" ->
                    productsData.addAll(customHib.getProductSearch(Product.class, column,value));
            case "os", "inches", "resolution", "battery", "ram", "weight", "storage" ->
                    productsData.addAll(customHib.getProductSearch(Phone.class, column, value));
            case "dia" ->
                    productsData.addAll(customHib.getProductSearch(Smartwatch.class, "diameter", value));
            case "contype" ->
                    productsData.addAll(customHib.getProductSearch(Headphones.class,"connection_type", value));
        }
    }

    public void reset() {
        columnSelectChoiceBox.getSelectionModel().clearSelection();
        searchTextField.setText("");
        loadProducts();
    }

    private void productTypeChoiceBoxSetup() {
        productTypes.add("Phone");
        productTypes.add("Smartwatch");
        productTypes.add("Headphones");
        productTypes.add("Other");
        productTypeChoiceBox.getItems().addAll(productTypes);

        productTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                osTextField.setDisable(true);
                inchesTextField.setDisable(true);
                resolutionTextField.setDisable(true);
                batteryTextField.setDisable(true);
                ramTextField.setDisable(true);
                weightTextField.setDisable(true);
                storageTextField.setDisable(true);
                if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Phone")){
                    osTextField.setDisable(false);
                    inchesTextField.setDisable(false);
                    resolutionTextField.setDisable(false);
                    batteryTextField.setDisable(false);
                    ramTextField.setDisable(false);
                    weightTextField.setDisable(false);
                    storageTextField.setDisable(false);
                    diameterTextField.setDisable(true);
                    connectionTypeTextField.setDisable(true);
                } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Smartwatch")){
                    diameterTextField.setDisable(false);
                    connectionTypeTextField.setDisable(true);
                } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Headphones")){
                    diameterTextField.setDisable(true);
                    connectionTypeTextField.setDisable(false);
                } else if(productTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Other")){
                    diameterTextField.setDisable(true);
                    connectionTypeTextField.setDisable(true);
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
                    if(selectedProduct.getWarehouse() == null){
                        warehouseSelectChoiceBox.getSelectionModel().clearSelection();
                    } else {
                        warehouseSelectChoiceBox.getSelectionModel().select(selectedProduct.getWarehouse().getName());
                    }
                    osTextField.setText("");
                    inchesTextField.setText("");
                    resolutionTextField.setText("");
                    batteryTextField.setText("");
                    ramTextField.setText("");
                    weightTextField.setText("");
                    storageTextField.setText("");
                    diameterTextField.setText("");
                    connectionTypeTextField.setText("");
                    if(type.equals("Phone")){
                        osTextField.setText(((Phone) selectedProduct).getOs());
                        inchesTextField.setText(String.valueOf(((Phone) selectedProduct).getInches()));
                        resolutionTextField.setText(((Phone) selectedProduct).getResolution());
                        batteryTextField.setText(String.valueOf(((Phone) selectedProduct).getBattery()));
                        ramTextField.setText(String.valueOf(((Phone) selectedProduct).getRam()));
                        weightTextField.setText(String.valueOf(((Phone) selectedProduct).getWeight()));
                        storageTextField.setText(String.valueOf(((Phone) selectedProduct).getStorage()));
                    } else if(type.equals("Smartwatch")){
                        diameterTextField.setText(String.valueOf(((Smartwatch) selectedProduct).getDiameter()));
                    } else if(type.equals("Headphones")){
                        connectionTypeTextField.setText(((Headphones) selectedProduct).getConnectionType());
                    } else if(type.equals("Other")){

                    }
                }
            }
        });
    }

    private void addWarehouseListSelectionListener() {
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

    private void setupColumns(){
        storageColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getStorage()).asObject();
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        osColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleStringProperty(((Phone) cellData.getValue()).getOs());
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        inchesColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleDoubleProperty(((Phone) cellData.getValue()).getInches()).asObject();
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        resolutionColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleStringProperty(((Phone) cellData.getValue()).getResolution());
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        batteryColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getBattery()).asObject();
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        ramColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getRam()).asObject();
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        weightColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Phone){
                return new SimpleIntegerProperty(((Phone) cellData.getValue()).getWeight()).asObject();
            } else if(cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        diameterColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Smartwatch){
                return new SimpleIntegerProperty(((Smartwatch) cellData.getValue()).getDiameter()).asObject();
            } else if(cellData.getValue() instanceof Phone || cellData.getValue() instanceof Headphones || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
        connectionTypeColumn.setCellValueFactory(cellData ->{
            if(cellData.getValue() instanceof Headphones){
                return new SimpleStringProperty(((Headphones) cellData.getValue()).getConnectionType());
            } else if(cellData.getValue() instanceof Phone || cellData.getValue() instanceof Smartwatch || cellData.getValue() instanceof Other){
                return null;
            }
            return null;
        });
    }
}
