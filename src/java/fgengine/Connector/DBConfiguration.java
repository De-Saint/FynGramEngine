/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Connector;

/**
 *
 * @author Pinky
 */
public class DBConfiguration {

    public String DB_USER_NAME;

    public String DB_PASSWORD;

    public String DB_URL;

    public String DB_DRIVER;

    public Integer DB_MAX_CONNECTIONS;

    public DBConfiguration() {
        init();
    }

    private static DBConfiguration dbConfiguration = new DBConfiguration();

    public static DBConfiguration getInstance() {
        return dbConfiguration;
    }

    private void init() {
        DB_MAX_CONNECTIONS = 100;
        DB_DRIVER = "com.mysql.jdbc.Driver";
        
//        auth settings local
        DB_USER_NAME = "root";
        DB_PASSWORD = "root";
        DB_URL = "jdbc:mysql://localhost:3306/fyngramdb?characterEncoding=utf8";

//        auth settings wmwrite app digital ocean
//    DB_URL = "jdbc:mysql://localhost:3306/fyngramdb";
//    DB_USER_NAME = "thewealt_WMUser1";
//    DB_PASSWORD = "@thewealthmarket123";


//    DB_URL = "jdbc:mysql://localhost:3306/fyngramdb";
//    DB_USER_NAME = "root";
//    DB_PASSWORD = "@DOwm123";
    }
}
