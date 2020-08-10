/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Pinky
 */
public class JDBCConnectionPool {

    List<Connection> availableConnections = new ArrayList<>();

    public JDBCConnectionPool() {
        initializeConnectionPool();
    }

    private void initializeConnectionPool() {
        while (!checkIfConnectionPoolIsFull()) {
            availableConnections.add(createNewConnectionForPool());
        }
    }

    private synchronized boolean checkIfConnectionPoolIsFull() {
        final int MAX_POOL_SIZE = DBConfiguration.getInstance().DB_MAX_CONNECTIONS;

        return availableConnections.size() >= MAX_POOL_SIZE;
    }

    //creating a new connection
    private Connection createNewConnectionForPool() {
        DBConfiguration dbConfiguration = DBConfiguration.getInstance();
        try {
            Class.forName(dbConfiguration.DB_DRIVER);
            Connection connection = (Connection) DriverManager.getConnection(dbConfiguration.DB_URL,
                    dbConfiguration.DB_USER_NAME, dbConfiguration.DB_PASSWORD);
            return connection;

        } catch (ClassNotFoundException | SQLException e) {
        }
        return null;
    }

    public synchronized Connection getConnectionFromPool() throws SQLException {
        Connection connection = null;
        if (availableConnections.size() > 0) {
            connection = (Connection) availableConnections.get(0);
            availableConnections.remove(0);
        }
        if (connection == null) {
            connection = createNewConnectionForPool();
        }
        return connection;
    }

    public synchronized void returnConnectionToPool(Connection connection) {
        availableConnections.add(connection);
    }

}
