package com.laula.demo.controller;

import com.laula.demo.module.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML
    public Button btnLoad;
    @FXML
    public Button btnModify;
    @FXML
    public Button btnDelete;
    @FXML
    public TextField txtFilterCode;
    @FXML
    private TableView<Product> tblProducts;
    @FXML
    private TableColumn colCode;
    @FXML
    private TableColumn colDescription;
    @FXML
    private TableColumn colStock;
    @FXML
    private TableColumn colPrice;

    private ObservableList<Product> products;
    private ObservableList<Product> filterProducts;

    static final String ERR = "Error";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        products = FXCollections.observableArrayList();
        filterProducts = FXCollections.observableArrayList();

        this.tblProducts.setItems(products);

        this.colCode.setCellValueFactory(new PropertyValueFactory("code"));
        this.colDescription.setCellValueFactory(new PropertyValueFactory("description"));
        this.colStock.setCellValueFactory(new PropertyValueFactory("stock"));
        this.colPrice.setCellValueFactory(new PropertyValueFactory("price"));
    }

    @FXML
    private void loadProducts() {
        try {
            //Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/laula/demo/save-product-view.fxml"));
            //Cargo la ventana
            Parent root = loader.load();

            //Tomo el controlador
            SaveProductController controller = loader.getController();
            controller.initAttributtes(products);

            //Creo el Scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            //Tomo el producto devuelta
            Product p = controller.getProduct();
            if (p != null) {
                products.add(p);
                if (p.getCode() == Integer.parseInt(this.txtFilterCode.getText())) {
                    this.filterProducts.add(p);
                }

                //Refresco la tabla
                this.tblProducts.refresh();
            }
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void modify() {
        Product p = this.tblProducts.getSelectionModel().getSelectedItem();

        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("Debes seleccionar un producto");
            alert.showAndWait();
        } else {
            try {
                // Cargo la vista
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/laula/demo/save-product-view.fxml"));

                // Cargo la ventana
                Parent root = loader.load();

                // Tomo el controlador
                SaveProductController controller = loader.getController();
                controller.initAttributtes(products, p);

                // Creo el Scene
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                // Tomo el producto devuelta
                Product pSelected = controller.getProduct();
                if (pSelected != null) {
                    if (pSelected.getCode() != Integer.parseInt(this.txtFilterCode.getText())) {
                        this.filterProducts.remove(pSelected);
                    }
                    this.tblProducts.refresh();
                }
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle(ERR);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void delete() {
        Product p = this.tblProducts.getSelectionModel().getSelectedItem();

        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("Debes seleccionar un producto");
            alert.showAndWait();
        } else {

            // Elimino el producto
            this.products.remove(p);
            this.filterProducts.remove(p);
            this.tblProducts.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Info");
            alert.setContentText("Se ha borrado el producto");
            alert.showAndWait();
        }
    }

    @FXML
    private void filterCode() {

        // Si el texto del código esta vacio, seteamos la tabla de productos con el original
        if (txtFilterCode.getText().isEmpty()) {
            this.tblProducts.setItems(products);
        } else {

            // Limpio la lista
            this.filterProducts.clear();

            for (Product p : this.products) {
                if (p.getCode() == Integer.parseInt(this.txtFilterCode.getText())) {
                    this.filterProducts.add(p);
                }
            }
            this.tblProducts.setItems(filterProducts);
        }
    }
}