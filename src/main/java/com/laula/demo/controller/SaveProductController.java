package com.laula.demo.controller;

import com.laula.demo.dao.ProductDAO;
import com.laula.demo.module.Product;
import com.laula.demo.persistence.ConnectionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    static final String ERR = "Error";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO document why this constructor is empty
    }

    public void initAttributtes(ObservableList<Product> list) {

        this.products = list;
    }

    public void initAttributtes(ObservableList<Product> products, Product p) {
        this.products = products;
        this.product = p;
        // cargo los datos de el producto
        this.txtCode.setText(this.product.getCode() + "");
        this.txtDescription.setText(this.product.getDescription());
        this.txtStock.setText(this.product.getStock() + "");
        this.txtPrice.setText(this.product.getPrice() + "");
        this.btnSave.setText("Modificar");
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea modificar este producto?", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText(this.product.getCode() + "");
                    if (alert.showAndWait().get() == ButtonType.YES) {
                        // Modifico el objeto
                        this.product.setCode(code);
                        this.product.setDescription(description);
                        this.product.setStock(stock);
                        this.product.setPrice(price);
                        try {
                            this.connectionBD.connectBase();
                            productDAO = new ProductDAO(connectionBD);
                            productDAO.save(p);
                        } catch (SQLException | ClassNotFoundException ex) {
                            Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            this.connectionBD.disconnectBase();
                        }
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setTitle("Información");
                        alert.setContentText("Se ha modificado correctamente");
                        alert.showAndWait();
                    }

                } else {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea crear este producto?", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Código: " + this.txtCode.getText() + "\n Descripción: "
                            + this.txtDescription.getText() + "\n Stock: "
                            + this.txtStock.getText() + "\n Precio: "
                            + this.txtPrice.getText() + "");

                    if (alert.showAndWait().get() == ButtonType.YES) {
                        try {
                            this.connectionBD.connectBase();
                            productDAO = new ProductDAO(connectionBD);
                            productDAO.save(p);
                        } catch (SQLException | ClassNotFoundException ex) {
                            Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            this.connectionBD.disconnectBase();
                        }
                        this.product = p;
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setTitle("Información");
                        alert.setContentText("Se ha añadido correctamente");
                        alert.showAndWait();
                    }
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
}