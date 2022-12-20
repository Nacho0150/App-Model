package com.laula.demo.controller;

import com.laula.demo.module.Product;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SaveProductController implements Initializable {

    @FXML
    public TextField txtCode;
    @FXML
    public TextField txtDescription;
    @FXML
    private TextField txtStock;
    @FXML
    private TextField txtPrice;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnLeave;

    private Product product;

    private ObservableList<Product> products;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Nada
    }

    public void initAttributtes(ObservableList<Product> products) {
        this.products = products;
    }

    public void initAttributtes(ObservableList<Product> products, Product p) {
        this.products = products;
        this.product = p;
        // cargo los datos de el producto
        this.txtCode.setText(p.getCode() + "");
        this.txtDescription.setText(p.getDescription());
        this.txtStock.setText(p.getStock() + "");
        this.txtPrice.setText(p.getPrice() + "");
    }

    @FXML
    private void save() {
        //Tomo los datos
        int code = Integer.parseInt(this.txtCode.getText());
        String description = this.txtDescription.getText();
        int stock = Integer.parseInt(this.txtStock.getText());
        int price = Integer.parseInt(this.txtPrice.getText());

        //Creo el producto
        Product p = new Product(code, description, stock, price);

        // Compruebo si la persona existe
        if (!products.contains(p)) {
            // Modificar
            if (this.product != null) {
                // Modifico el objeto
                this.product.setCode(code);
                this.product.setDescription(description);
                this.product.setStock(stock);
                this.product.setPrice(price);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Información");
                alert.setContentText("Se ha modificado correctamente");
                alert.showAndWait();
            }else {
                this.product = p;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Información");
                alert.setContentText("Se ha añadido correctamente");
                alert.showAndWait();
            }
            // Cerrar ventana
            Stage stage = (Stage) this.btnSave.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("El producto ya existe");
            alert.showAndWait();
        }
    }

    @FXML
    private void leave() {
        this.product = null;
        // Cerrar ventana
        Stage stage = (Stage) this.btnLeave.getScene().getWindow();
        stage.close();
    }

    public Product getProduct() {
        return product;
    }
}