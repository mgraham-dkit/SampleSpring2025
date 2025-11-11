package daos.daos;

import java.sql.Connection;

public interface Connector {
    public Connection getConnection();
    public void freeConnection();
}
