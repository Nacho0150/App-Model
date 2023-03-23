package com.app.demo.controller;

import com.app.demo.dao.ProductDAO;
import com.app.demo.errors.ErrorService;
import com.app.demo.module.Product;
import com.app.demo.persistence.ConnectionBD;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;

public class ProductController implements Initializable {

    @FXML
    public Button btnLoad;
    @FXML
    public Button btnModify;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnAdicStock;
    @FXML
    public Button btnDescStock;
    @FXML
    public TextField txtFilter;
    @FXML
    public Label labFilter;
    @FXML
    public Hyperlink linkCreater;
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
    @FXML
    private Hyperlink hyperlink = new Hyperlink();

    private ObservableList<Product> products;
    private ObservableList<Product> filterProducts;
    private ObservableList<Product> list;
    private final ConnectionBD connectionBD = new ConnectionBD();
    private final ObjectProperty<Product> objProduct = new SimpleObjectProperty<>();
    private ProductDAO productDAO;
    static final String ERR = "Error";
    static final String ASSI = "[^\\p{ASCII}]";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(labFilter, 14.0);
        AnchorPane.setLeftAnchor(labFilter, 10.0);

        AnchorPane.setTopAnchor(txtFilter, 12.0);
        AnchorPane.setLeftAnchor(txtFilter, 57.0);

        AnchorPane.setTopAnchor(tblProducts, 50.0);
        AnchorPane.setLeftAnchor(tblProducts, 10.0);
        AnchorPane.setRightAnchor(tblProducts, 113.0);
        AnchorPane.setBottomAnchor(tblProducts, 20.0);

        AnchorPane.setTopAnchor(btnLoad, 50.0);
        AnchorPane.setRightAnchor(btnLoad, 13.0);

        AnchorPane.setTopAnchor(btnModify, 84.0);
        AnchorPane.setRightAnchor(btnModify, 13.0);

        AnchorPane.setTopAnchor(btnDelete, 118.0);
        AnchorPane.setRightAnchor(btnDelete, 13.0);

        AnchorPane.setTopAnchor(btnAdicStock, 164.0);
        AnchorPane.setRightAnchor(btnAdicStock, 13.0);

        AnchorPane.setTopAnchor(btnDescStock, 198.0);
        AnchorPane.setRightAnchor(btnDescStock, 13.0);

        AnchorPane.setRightAnchor(linkCreater, 2.0);
        AnchorPane.setBottomAnchor(linkCreater, 0.0);

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
            Stage stage = new Stage();
            //Cargo la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/save-product-view.fxml"));
            //Cargo la ventana
            Parent root = loader.load();

            stage.setTitle("App Model");
            URL resource = getClass().getResource("/com/app/demo/images/Ignacio-Ibañez.png");
            Image image = new Image(resource.toString());
            stage.getIcons().add(image);

            //Tomo el controlador
            SaveProductController controller = loader.getController();
            controller.initAttributtes(list);

            //Creo el Scene
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.showAndWait();

            //Tomo el producto devuelta
            Product p = controller.getProduct();
            if (p != null) {
                list.add(p);
                String cod = this.txtFilter.getText();
                String desc = this.txtFilter.getText();
                listProduct();
                if (p.getCode().equals(cod) || p.getDescription().equals(desc)) {
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
                Stage stage = new Stage();

                // Cargo la vista
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/demo/save-product-view.fxml"));

                // Cargo la ventana
                Parent root = loader.load();

                stage.setTitle("Laula");
                URL resource = getClass().getResource("/com/app/demo/images/laula.png");
                Image image = new Image(resource.toString());
                stage.getIcons().add(image);

                // Tomo el controlador
                SaveProductController controller = loader.getController();
                controller.initAttributtes(list, p);

                // Creo el Scene
                Scene scene = new Scene(root);
                stage.initModality(Modality.APPLICATION_MODAL);
                scene.setFill(Color.TRANSPARENT);
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
    private void adiStock() throws SQLException, ClassNotFoundException, ErrorService {
        Product p = this.tblProducts.getSelectionModel().getSelectedItem();

        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("Debes seleccionar un producto");
            alert.showAndWait();
        } else {
            this.connectionBD.connectBase();
            productDAO = new ProductDAO(connectionBD);
            // Adiciono al stock
            p.setStock(p.getStock() + 1);
            productDAO.adicionarDescontarStock(p);
            this.filterProducts.remove(p);
            this.tblProducts.refresh();
            listProduct();
        }
    }

    @FXML
    private void descStock() throws SQLException, ClassNotFoundException, ErrorService {
        Product p = this.tblProducts.getSelectionModel().getSelectedItem();

        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("Debes seleccionar un producto");
            alert.showAndWait();
        } else {
            this.connectionBD.connectBase();
            productDAO = new ProductDAO(connectionBD);
            // Descuento del stock
            p.setStock(p.getStock() - 1);
            productDAO.adicionarDescontarStock(p);
            this.filterProducts.remove(p);
            this.tblProducts.refresh();
            listProduct();
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
            alert.setHeaderText("Código: " + this.objProduct.get().getCode() +
                    "\n Descripción: " + this.objProduct.get().getDescription() +
                    "\n Stock: " + this.objProduct.get().getStock() +
                    "\n Precio: " + this.objProduct.get().getPrice() + "");
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
                String fildesccode = this.txtFilter.getText();
                String pcode = p.getCode();

                //Normaliza a las variables, sacandole tildes o lo que sea para que se puedan comparar
                String pdescNormalizada1 = Normalizer.normalize(pdesc, Normalizer.Form.NFD)
                        .replaceAll(ASSI, "");
                String fildesccodeNormalizada2 = Normalizer.normalize(fildesccode, Normalizer.Form.NFD)
                        .replaceAll(ASSI, "");
                String pcodeNormalizada1 = Normalizer.normalize(pcode, Normalizer.Form.NFD)
                        .replaceAll(ASSI, "");

                if (pdescNormalizada1.toLowerCase().contains(fildesccodeNormalizada2.toLowerCase()) || pcodeNormalizada1.toLowerCase().contains(fildesccodeNormalizada2.toLowerCase())) {
                    this.filterProducts.add(p);
                }
            }
            this.tblProducts.setItems(filterProducts);
        }
    }

    @FXML
    private void openLink(ActionEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://sites.google.com/view/miportafolio-ignacio/inicio?authuser=0"));
    }
}