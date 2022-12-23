package com.laula.demo.controller;

import com.laula.demo.dao.ProductDAO;
import com.laula.demo.module.Product;
import com.laula.demo.persistence.ConnectionBD;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private final ConnectionBD connectionBD = new ConnectionBD();
    private ProductDAO productDAO;
    ObservableList<Product> listProduct = FXCollections.observableArrayList();
    private ObjectProperty<Product> objProduct = new SimpleObjectProperty<>();
    static final String ERR = "Error";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            listProduct();
        } catch (SQLException ex) {
            Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    public void initAttributtes(ObservableList<Product> products) {

        this.products = products;
    }

    public void initAttributtes(ObservableList<Product> products, Product p) {
        this.products = products;
        this.product = p;
        // cargo los datos de el producto
        this.txtCode.setText(this.product.getCode() + "");
        this.txtDescription.setText(this.product.getDescription());
        this.txtStock.setText(this.product.getStock() + "");
        this.txtPrice.setText(this.product.getPrice() + "");
    }

    @FXML
    private void save() throws SQLException {
        //Tomo los datos
        long code = Long.parseLong(this.txtCode.getText());
        String description = this.txtDescription.getText();
        int stock = Integer.parseInt(this.txtStock.getText());
        int price = Integer.parseInt(this.txtPrice.getText());

        //Creo el producto
        Product p = new Product(code, description, stock, price);

        // Compruebo si la producto existe
        try {
            if (!products.contains(p)) {
                // Modificar
                if (this.product != null) {
                    // Modifico el objeto
                    this.product.setCode(code);
                    this.product.setDescription(description);
                    this.product.setStock(stock);
                    this.product.setPrice(price);

                    try {
                        this.connectionBD.connectBase();
                        productDAO = new ProductDAO(connectionBD);
                        productDAO.save(p);
                        listProduct();
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        this.connectionBD.disconnectBase();
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Información");
                    alert.setContentText("Se ha modificado correctamente");
                    alert.showAndWait();
                } else {
                    try {
                        this.connectionBD.connectBase();
                        productDAO = new ProductDAO(connectionBD);
                        productDAO.save(p);
                        listProduct();
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        this.connectionBD.disconnectBase();
                    }
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
                alert.setTitle(ERR);
                alert.setContentText("El producto ya existe");
                alert.showAndWait();
            }
        } catch (Exception ex) {
            Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText(ex.getMessage());
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

    public void listProduct() throws SQLException {
        try {
            this.connectionBD.connectBase();
            productDAO = new ProductDAO(connectionBD);
            listProduct.setAll(productDAO.getAll());
        } catch (SQLException ex) {
            Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            this.connectionBD.disconnectBase();
        }
    }
}