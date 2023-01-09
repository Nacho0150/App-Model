package com.laula.demo.dao;

import com.laula.demo.errors.ErrorService;
import com.laula.demo.module.Product;
import com.laula.demo.persistence.ConnectionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProductDAO extends ConnectionBD{

    private final ConnectionBD connectionBD;

    public ProductDAO(ConnectionBD connectionBD) {

        this.connectionBD = connectionBD;
    }

    public void save(Product p) throws SQLException, ErrorService {
        PreparedStatement pst = null;
        try {
            String sql = "INSERT INTO product(\n"
                    + "code, description, stock, price)\n"
                    + " VALUES ('" + p.getCode() + "', '" + p.getDescription() + "', '" + p.getStock() + "', '" + p.getPrice() + "')";
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

//    public void updateProduct(Product product) throws ErrorService {
//        try {
//            // SENTENCIA SQL DE MODIFICACIÓN
//            String sql = "UPDATE producto SET nombre = '" + product.getName()
//                    + "' WHERE codigo = '" + product.getCode() + "';";
//
//            // SE MUESTRA LA CADENA RESULTANTE
//            System.out.println(sql);
//            System.out.println("");
//
//            insertModifyDelete(sql);
//        } catch (MyException e) {
//            System.out.println(e.getMessage());
//            throw new MyException("ERROR AL MODIFICAR EL NOMBRE DEL PRODUCTO");
//        }
//    }

    public void deleteProduct(long code) throws ErrorService, SQLException {
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

                product.setCode(result.getLong("code"));
                product.setDescription(result.getString("description").trim());
                product.setStock(result.getInt("stock"));
                product.setPrice(result.getInt("price"));
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