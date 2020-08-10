/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pinky
 */
public class DataSource {

    static JDBCConnectionPool pool = new JDBCConnectionPool();

    public Connection getConnection()throws ClassNotFoundException {
        Connection connection = null;
        try {
            connection = pool.getConnectionFromPool();
        } catch (SQLException ex) {
            Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
    
     public static void returnConnection(Connection connection) {
        pool.returnConnectionToPool(connection);
    }
}
