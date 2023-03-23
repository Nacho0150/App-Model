package com.app.demo.dao;

import com.app.demo.errors.ErrorService;
import com.app.demo.module.Product;
import com.app.demo.persistence.ConnectionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProductDAO extends ConnectionBD {

    private final ConnectionBD connectionBD;

    static final String WC = "' WHERE code = '";

    static final String ERR = "ERROR AL MODIFICAR EL PRODUCTO";

    public ProductDAO(ConnectionBD connectionBD) {

        this.connectionBD = connectionBD;
    }

    public void save(Product p) throws SQLException, ErrorService {
        PreparedStatement pst = null;
        try {
            String sql = "INSERT INTO product(\n"
                    + "code, description, stock, price)\n"
                    + " VALUES ('" + p.getCode().toUpperCase() + "', '" + p.getDescription() + "', '" + p.getStock() + "', '" + p.getPrice() + "')";
            pst = this.connectionBD.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.executeUpdate();

        } catch (SQLException ex) {
            throw new ErrorService("Error al ejecutar sentencia");
        } finally {
            assert pst != null;
            pst.close();
            disconnectBase();
        }
    }

    public void updateProductCode(Product product, Product p) throws ErrorService {
        try {
            // SENTENCIA SQL DE MODIFICACIÓN
            String sql = "UPDATE product SET code = '" + product.getCode()
                    + WC + p.getCode() + "';";

            insertModifyDelete(sql);
        } catch (ErrorService | SQLException e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService(ERR);
        }
    }

    public void updateProductDescription(Product product) throws ErrorService {
        try {
            // SENTENCIA SQL DE MODIFICACIÓN
            String sql = "UPDATE product SET description = '" + product.getDescription()
                    + WC + product.getCode() + "';";

            insertModifyDelete(sql);
        } catch (ErrorService | SQLException e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService(ERR);
        }
    }

    public void updateProductStock(Product product) throws ErrorService {
        try {
            // SENTENCIA SQL DE MODIFICACIÓN
            String sql = "UPDATE product SET stock = '" + product.getStock()
                    + WC + product.getCode() + "';";

            insertModifyDelete(sql);
        } catch (ErrorService | SQLException e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService(ERR);
        }
    }

    public void updateProductPrice(Product product) throws ErrorService {
        try {
            // SENTENCIA SQL DE MODIFICACIÓN
            String sql = "UPDATE product SET price = '" + product.getPrice()
                    + WC + product.getCode() + "';";

            insertModifyDelete(sql);
        } catch (ErrorService | SQLException e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService(ERR);
        }
    }

    public void adicionarDescontarStock(Product product) throws ErrorService, SQLException {
        try {
            String sql = "UPDATE product SET stock = '" + product.getStock()
                    + WC + product.getCode() + "';";

            connectionBD.insertModifyDelete(sql);
        } catch (Exception e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService("ERROR AL DESCONTAR DEL STOCK");
        } finally {
            connectionBD.disconnectBase();
        }
    }

    public void deleteProduct(String code) throws ErrorService, SQLException {
        try {
            String sql = "DELETE FROM product WHERE code = '" + code + "';";

            connectionBD.insertModifyDelete(sql);
        } catch (Exception e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService("ERROR AL BORRAR AL PRODUCTO");
        } finally {
            connectionBD.disconnectBase();
        }
    }

    public List<Product> getAll() throws SQLException, ErrorService {
        try {
            String sql = "SELECT * FROM product;";

            consultBase(sql);

            Product product = null;

            List<Product> products = new ArrayList<>();
            while (result.next()) {
                product = new Product();

                product.setCode(result.getString("code"));
                product.setDescription(result.getString("description").trim());
                product.setStock(result.getInt("stock"));
                product.setPrice(result.getString("price"));
                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            Logger.getLogger(e.getMessage());
            throw new ErrorService("ERROR AL LISTAR LOS PRODUCTOS");
        } finally {
            disconnectBase();
        }
    }
}