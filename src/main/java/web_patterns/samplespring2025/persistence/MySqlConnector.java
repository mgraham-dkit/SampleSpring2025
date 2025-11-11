package daos.daos;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class MySqlConnector implements Connector{
    private Properties properties;
    private Connection conn;

    public MySqlConnector(String propertiesFilename){
        properties = new Properties();
        try {
            // Get the path to the specified properties file
            String rootPath = Thread.currentThread().getContextClassLoader().getResource(propertiesFilename).getPath();
            // Load in all key-value pairs from properties file
            properties.load(new FileInputStream(rootPath));
        }catch(IOException e){
            System.out.println("An exception occurred when attempting to load properties from \"" + propertiesFilename + "\": " + e.getMessage());

            log.error("Error: " + e.getMessage());
        }
    }


    public Connection getConnection(){
        conn = null;


        //String driver = "com.mysql.cj.jdbc.Driver";
        String driver = properties.getProperty("driver", "com.mysql.cj.jdbc.Driver");
        //String url = "jdbc:mysql://127.0.0.1:3306/classicmodels";
        String url = properties.getProperty("url", "jdbc:mysql://127.0.0.1:3306/");
        String database = properties.getProperty("database", "classicmodels");
        //String username = "root";
        String username = properties.getProperty("username", "root");
        //String password = "";
        String password = properties.getProperty("password", "");

        try {
            // Load the database driver
            Class.forName(driver);
            // Get a connection to the database
            conn = DriverManager.getConnection(url+database, username, password);
        }catch(SQLException e){
            log.error("Connection could not be established - incorrect URL or database not switched on. \n Exception:" +
                    " {}", e.getMessage());
        }catch(ClassNotFoundException e){
            log.error("Driver files have not been loaded. Please check pom driver dependencies details. \n Exception:" +
                    " {}", e.getMessage());
        }

        return conn;
    }

    public void freeConnection(){
        if(conn != null){
            try{
                conn.close();
                conn = null;
            }catch (SQLException e){
                log.error("An exception occurred when attempting to close the connection to the database \n " +
                        "Exception:" +
                        " {}", e.getMessage());
            }
        }
    }
}
