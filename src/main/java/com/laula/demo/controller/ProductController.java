package com.laula.demo.controller;

import com.laula.demo.dao.ProductDAO;
import com.laula.demo.errors.ErrorService;
import com.laula.demo.module.Product;
import com.laula.demo.persistence.ConnectionBD;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private ObservableList<Product> list;
    private final ConnectionBD connectionBD = new ConnectionBD();
    private ObjectProperty<Product> objProduct = new SimpleObjectProperty<>();
    private ProductDAO productDAO;
    static final String ERR = "Error";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        products = FXCollections.observableArrayList();
        filterProducts = FXCollections.observableArrayList();
        list = FXCollections.observableArrayList();

        this.colCode.setCellValueFactory(new PropertyValueFactory("code"));
        this.colDescription.setCellValueFactory(new PropertyValueFactory("description"));
        this.colStock.setCellValueFactory(new PropertyValueFactory("stock"));
        this.colPrice.setCellValueFactory(new PropertyValueFactory("price"));

        try {
            listProduct();
        } catch (SQLException | ErrorService e) {
//            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e.getMessage());
        }
        tblProducts.setItems(list);
        objProduct.bind(tblProducts.getSelectionModel().selectedItemProperty());
    }

    @FXML
    private void loadProducts() throws ErrorService {
        try {
            //Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/laula/demo/save-product-view.fxml"));
            //Cargo la ventana
            Parent root = loader.load();

            //Tomo el controlador
            SaveProductController controller = loader.getController();
            controller.initAttributtes(list);

            //Creo el Scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            //Tomo el producto devuelta
            Product p = controller.getProduct();
            if (p != null) {
                list.add(p);
                long cod = Long.parseLong((this.txtFilterCode.getText()));
                listProduct();
                if (p.getCode() == cod) {
                    this.filterProducts.add(p);
                }

                //Refresco la tabla
                this.tblProducts.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (ErrorService | SQLException e) {
            throw new ErrorService(e.getMessage());
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
                    long cod = Long.parseLong((this.txtFilterCode.getText()));
                    if (pSelected.getCode() != cod) {
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
    private void delete() throws SQLException, ClassNotFoundException, ErrorService {
        Product p = this.tblProducts.getSelectionModel().getSelectedItem();

        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("Debes seleccionar un producto");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea eliminar este producto?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(this.objProduct.get().getCode() + "");
            if (alert.showAndWait().get() == ButtonType.YES) {
                this.connectionBD.connectBase();
                productDAO = new ProductDAO(connectionBD);
                // Elimino el producto
                productDAO.deleteProduct(objProduct.get().getCode());
                this.filterProducts.remove(p);
                this.tblProducts.refresh();
                listProduct();
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("Info");
                alert.setContentText("Se ha borrado el producto");
                alert.showAndWait();
            }
        }
    }

    public void listProduct() throws SQLException, ErrorService {
        try {
            this.connectionBD.connectBase();
            productDAO = new ProductDAO(connectionBD);
            list.setAll(productDAO.getAll());
        } catch (SQLException ex) {
            Logger.getLogger(SaveProductController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            throw new ErrorService(e.getMessage());
        } finally {
            this.connectionBD.disconnectBase();
        }
    }

    @FXML
    private void filterCode() {

        // Si el texto del código esta vacio, seteamos la tabla de productos con el original
        if (txtFilterCode.getText().isEmpty()) {
            this.tblProducts.setItems(list);
        } else {

            // Limpio la lista
            this.filterProducts.clear();

            for (Product p : this.list) {
                if (p.getCode() == Integer.parseInt(this.txtFilterCode.getText())) {
                    this.filterProducts.add(p);
                }
            }
            this.tblProducts.setItems(filterProducts);
        }
    }
}