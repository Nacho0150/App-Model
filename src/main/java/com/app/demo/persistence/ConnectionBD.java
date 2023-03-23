package com.app.demo.persistence;

import com.app.demo.errors.ErrorService;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionBD {
    // ADMINISTRA NUESTRA CONEXIÓN
    public Connection connection;
    // INSTRUCCIÓN DE CONSULTA
    private Statement statement;
    // MANIPULA LOS RESULTADOS
    protected ResultSet result = null;
    private static final String IP = "localhost";
    private static final String PORT = "3306";
    private static final String BD = "app";
    private static final String USER = "root";
    private static final String PASS = "root";
    private final String Driver = "com.mysql.cj.jdbc.Driver";
    static final String ERR = "ERROR";

    public ConnectionBD(){
        // TODO document why this constructor is empty
    }

    public void connectBase() throws ClassNotFoundException, SQLException {
        try {
            // CARGA EL DRIVER
            Class.forName(Driver);
            // ESTABLECE LA CONEXIÓN
            connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + "/" + BD + "?useSSL=false",
                    USER, PASS);
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
    public void insertModifyDelete(String sql) throws ErrorService, SQLException {
        try {
            connectBase();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (ClassNotFoundException | SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new ErrorService("Error al realizar el rollback");
            }
            throw new ErrorService("Error al ejecutar sentencia");
        } finally {
            disconnectBase();
        }
    }

    /**
     * @param sql ESTA VARIABLE TIENE LO QUE QUIERE HACER EL USUARIO
     */
    public void consultBase(String sql) throws SQLException, ErrorService {
        try {
            connectBase();
            statement = connection.createStatement();
            result = statement.executeQuery(sql);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new ErrorService("Error al consultar");
        }
    }


    public void disconnectBase() throws SQLException {
        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
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