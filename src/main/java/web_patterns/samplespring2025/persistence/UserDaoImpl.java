package web_patterns.samplespring2025.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import web_patterns.samplespring2025.utils.PasswordHasher;

import java.sql.*;
import java.util.Scanner;

@Slf4j
@Repository
public class UserDaoImpl implements UserDao{
    private Connector connector;

    public UserDaoImpl(Connector connector){
        this.connector = connector;
    }


    public void closeConnection(){
        connector.freeConnection();
    }

    public boolean register(String username, String password) throws SQLException {
        Connection conn = connector.getConnection();
        if (conn == null) {
            throw new SQLException("login(): Could not establish connection to database.");
        }

        if(username == null){
            throw new IllegalArgumentException("Cannot register with a null username");
        }

        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Cannot register with a null or blank password");
        }

        String hashedPassword = PasswordHasher.hashPassword(password);

        int addedRows = 0;
        try(PreparedStatement ps =
                    conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            addedRows = ps.executeUpdate();

        }catch(SQLIntegrityConstraintViolationException e){
            log.error("register() - Username \"{}\" unavailable", username);
        }catch(SQLException e){
            log.error("register() - The SQL query could not be prepared or executed. \nException: {}", e.getMessage());
            throw e;
        }
        return addedRows == 1;
    }

    public boolean login(String username, String password) throws SQLException {
        Connection conn = connector.getConnection();
        if (conn == null) {
            throw new SQLException("login(): Could not establish connection to database.");
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users where username = ?")) {
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()) {
                // If user found with supplied username, verify password against stored hash
                if(rs.next()){
                    String hashedPassword = rs.getString("password");
                    return PasswordHasher.verifyPassword(password, hashedPassword);
                }
                // No user found for that username - return false
                return false;
            }catch(SQLException e){
                log.error("login(): An issue occurred when running the query or processing " +
                        "the resultset. \nException: {}", e.getMessage());
                throw e;
            }
        }catch(SQLException e){
            log.error("login() - The SQL query could not be prepared. \nException: {}",
                    e.getMessage());
            throw e;
        }
    }

    public static void main(String [] args) throws SQLException {
        Connector connector = new MySqlConnector("properties/database.properties");
        UserDao userDao = new UserDaoImpl(connector);
        Scanner input = new Scanner(System.in);

        System.out.print("Username: ");
        String username = input.nextLine().toLowerCase();

        System.out.print("Password: ");
        String password = input.nextLine();

        try {
            boolean registered = userDao.register(username, password);
            if (registered) {
                System.out.println("Welcome to the system, " + username);
            } else {
                System.out.println("Registration failed.");
            }
        }catch(SQLIntegrityConstraintViolationException e){
            System.out.println("Username unavailable.");
        }
    }
}
