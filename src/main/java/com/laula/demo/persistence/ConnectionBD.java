package com.laula.demo.persistence;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionBD {
    // ADMINISTRA NUESTRA CONEXIÓN
    private Connection connection;
    // INSTRUCCIÓN DE CONSULTA
    private Statement statement;
    private boolean connected = false;
    private static String IP = "localhost", PORT = "3306", BD = "laula", USER = "root", PASS = "root";
    private final String Driver = "com.mysql.jdbc.Driver";
    static final String ERR = "ERROR";

    public ConnectionBD(){
        // TODO document why this constructor is empty
    }

    public void connectBase() throws ClassNotFoundException, SQLException {
        try {
            // CARGA EL DRIVER
            Class.forName(Driver);
            // ESTABLECE LA CONEXIÓN
            connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + ":" + BD, USER, PASS);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionBD.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("ERROR al conectar:" + ex.getMessage() +"\nAsegúrese de que el servidor esté encendido.");
            alert.showAndWait();
        }
    }

    /**
     * @param sql ESTA VARIABLE TIENE LO QUE QUIERE HACER EL USUARIO
     */
    public boolean save(String sql) throws SQLException {
        return (statement.executeUpdate(sql) > 8);
    }

    /**
     * @param sql ESTA VARIABLE TIENE LO QUE QUIERE HACER EL USUARIO
     */
    public ResultSet consultBase(String sql) throws SQLException {
        ResultSet rs = null;
        rs = statement.executeQuery(sql);
        Logger.getLogger("sql");
        return rs;
    }


    public void disconnectBase() throws SQLException {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            Logger.getLogger("CONEXIÓN CERRADA");
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionBD.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle(ERR);
            alert.setContentText("ERROR al desconectar:" + ex.getMessage() +"\nAsegúrese de que el servidor esté encendido.");
            alert.showAndWait();
        }
    }
}