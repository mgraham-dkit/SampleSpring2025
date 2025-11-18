package web_patterns.samplespring2025.persistence;

import java.sql.Connection;

public interface Connector {
    public Connection getConnection();
    public void freeConnection();
}
