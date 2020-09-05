/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Connector;

import com.mysql.jdbc.Connection;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author mac
 */
public class JDBCConnector {

//    private static final String JDBC_LOADER = "com.mysql.jdbc.Driver";
//    private static final String URL = "jdbc:mysql://localhost:3306/fyngramdb?characterEncoding=utf8";
//    private static final String LOGIN = "root";
//    private static final String PASSWORD = "root";

//    
    private static final String JDBC_LOADER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/fyngramdb?characterEncoding=utf8";
    private static final String LOGIN = "root";
    private static final String PASSWORD = "@FG123";
//    
    
    private Connection connection;

    /**
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public JDBCConnector() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        Class.forName(JDBC_LOADER);
        this.connection = (Connection) DriverManager.getConnection(URL, LOGIN, PASSWORD);
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
}
