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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.Normalizer;
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
    public TextField txtFilter;
    @FXML
    public Label labFilter;
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
    private final ObjectProperty<Product> objProduct = new SimpleObjectProperty<>();
    private ProductDAO productDAO;
    static final String ERR = "Error";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(labFilter, 12.0);
        AnchorPane.setLeftAnchor(labFilter, 10.0);

        AnchorPane.setTopAnchor(txtFilter, 10.0);
        AnchorPane.setLeftAnchor(txtFilter, 53.0);

        AnchorPane.setTopAnchor(tblProducts, 50.0);
        AnchorPane.setLeftAnchor(tblProducts, 10.0);
        AnchorPane.setRightAnchor(tblProducts, 100.0);
        AnchorPane.setBottomAnchor(tblProducts, 10.0);

        AnchorPane.setTopAnchor(btnLoad, 50.0);
        AnchorPane.setRightAnchor(btnLoad, 10.0);

        AnchorPane.setTopAnchor(btnModify, 80.0);
        AnchorPane.setRightAnchor(btnModify, 10.0);

        AnchorPane.setTopAnchor(btnDelete, 110.0);
        AnchorPane.setRightAnchor(btnDelete, 10.0);

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
                long cod = Long.parseLong((this.txtFilter.getText()));
                String desc = this.txtFilter.getText();
                listProduct();
                if (p.getCode() == cod || p.getDescription().equals(desc)) {
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
                controller.initAttributtes(list, p);

                // Creo el Scene
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                // Tomo el producto devuelta
                Product pSelected = controller.getProduct();
                if (pSelected != null) {
//                    long cod = Long.parseLong((this.txtFilterCode.getText()));
//                    if (pSelected.getCode() != cod) {
                        this.filterProducts.remove(pSelected);
//                    }
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
    private void filter() {

        // Si el texto del código o la descripción está vacio, seteamos la tabla de productos con el original
        if (txtFilter.getText().isEmpty()) {
            this.tblProducts.setItems(list);
        } else {
            // Limpio la lista
            this.filterProducts.clear();

            for (Product p : this.list) {
                String pdesc = p.getDescription();
                String fildesc = this.txtFilter.getText();

                //Normaliza a las variables, sacandole tildes o lo que sea para que se puedan comparar
                String cadenaNormalizada1 = Normalizer.normalize(pdesc, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");
                String cadenaNormalizada2 = Normalizer.normalize(fildesc, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");

                if (cadenaNormalizada1.equalsIgnoreCase(cadenaNormalizada2) || String.valueOf(p.getCode()).equals(this.txtFilter.getText())) {
                    this.filterProducts.add(p);
                }
            }
            this.tblProducts.setItems(filterProducts);
        }
    }
}