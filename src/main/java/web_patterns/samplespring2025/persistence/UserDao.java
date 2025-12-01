package web_patterns.samplespring2025.persistence;

import java.sql.SQLException;

public interface UserDao {
    public boolean register(String username, String password) throws SQLException;
    public boolean login(String username, String password) throws SQLException;
    public void closeConnection();
}
