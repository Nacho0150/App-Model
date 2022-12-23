package com.laula.demo.dao;

import com.laula.demo.module.Product;
import com.laula.demo.persistence.ConnectionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends ConnectionBD{

    private final ConnectionBD connectionBD;

    public ProductDAO(ConnectionBD connectionBD) {

        this.connectionBD = connectionBD;
    }

    public void save(Product p) throws SQLException {
        String sql = null;
        if (p.getCode() != 0L) {
            sql = "INSERT INTO product(\n"
                    + "code, description, stock, price)\n"
                    + " VALUES ('" + p.getCode() + "', '" + p.getDescription() + "', '" + p.getStock() + "', '" + p.getPrice() + "')";
        }
        PreparedStatement pst = this.connectionBD.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.executeUpdate();
    }

    public List<Product> getAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        Product p = null;
        ResultSet rs = this.connectionBD.consultBase("SELECT code, description, stock, price FROM product;");
        while (rs.next()) {
            p = new Product();
            p.setCode(rs.getLong("code"));
            p.setDescription(rs.getString("description").trim());
            p.setStock(rs.getInt("stock"));
            p.setPrice(rs.getInt("price"));
            list.add(p);
        }
        return list;
    }
}