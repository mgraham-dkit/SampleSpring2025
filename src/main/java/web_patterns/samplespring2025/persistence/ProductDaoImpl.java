package daos.daos;



import daos.entities.Product;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductDaoImpl implements ProductDao {
    private Connector connector;

    public ProductDaoImpl(Connector connector){
        this.connector = connector;
    }

    public void closeConnection(){
        connector.freeConnection();
    }

    public List<Product> getAllProducts() throws SQLException{
        Connection conn = connector.getConnection();
        if(conn == null){
            throw new SQLException("getAllProducts(): Could not establish connection to database.");
        }

        ArrayList<Product> products = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM products")) {
            try(ResultSet rs = ps.executeQuery()) {
                // Loop through the result set
                while(rs.next()){
                    Product product = mapProductRow(rs);
                    products.add(product);
                }
            }catch(SQLException e){
                log.error("getAllProducts(): An issue occurred when running the query or processing " +
                        "the resultset. \nException: {}", e.getMessage());
                throw e;
            }
        }catch(SQLException e){
            log.error("getAllProducts() - The SQL query could not be prepared. \nException: {}", e.getMessage());
            throw e;
        }
        return products;
    }

    private static Product mapProductRow(ResultSet rs) throws SQLException {
        return Product.builder()
                .productCode(rs.getString("productCode"))
                .productName(rs.getString("productName"))
                .productLine(rs.getString("productLine"))
                .productScale(rs.getString("productScale"))
                .productVendor(rs.getString("productVendor"))
                .productDescription(rs.getString("productDescription"))
                .quantityInStock(rs.getInt("quantityInStock"))
                .buyPrice(rs.getDouble("buyPrice"))
                .msrp(rs.getDouble("MSRP"))
                .build();
    }


    public List<Product> getAllProductsContainingKeyword(String keyword) throws SQLException{
        Connection conn = connector.getConnection();
        if(conn == null){
            throw new SQLException("getAllProductsContainingKeyword(): Could not establish connection to database.");
        }

        ArrayList<Product> products = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE productDescription LIKE ?")) {
            ps.setString(1, "%" + keyword + "%");
            try(ResultSet rs = ps.executeQuery()) {
                // Loop through the result set
                while(rs.next()){
                    Product product = mapProductRow(rs);
                    products.add(product);
                }
            }catch(SQLException e){
                log.error("getAllProductsContainingKeyword(): An issue occurred when running the query or processing " +
                                "the resultset. \nException: {}", e.getMessage());
                throw e;
            }
        }catch(SQLException e){
            log.error("getAllProductsContainingKeyword() - The SQL query could not be prepared. \nException: {}",
                    e.getMessage());
            throw e;
        }
        return products;
    }

    public Product getProductByCode(String prodCode) throws SQLException{
        Connection conn = connector.getConnection();
        if(conn == null){
            throw new SQLException("getProductByCode(): Could not establish connection to database.");
        }

        Product product = null;
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE productCode = ?")) {
            ps.setString(1, prodCode);
            try(ResultSet rs = ps.executeQuery()) {
                // Loop through the result set
                if(rs.next()){
                    product = mapProductRow(rs);
                }
            }catch(SQLException e){
                log.error("getProductByCode(): An issue occurred when running the query or processing the resultset. " +
                                "\nException: {}",
                        e.getMessage());
                throw e;
            }
        }catch(SQLException e){
            log.error("getProductByCode() - The SQL query could not be prepared. \nException: {}", e.getMessage());
            throw e;
        }
        return product;
    }

    public Product deleteProductByCode(String prodCode) throws SQLException{
        Product removed = getProductByCode(prodCode);
        if(removed == null){
            return removed;
        }

        Connection conn = connector.getConnection();
        if(conn == null){
            throw new SQLException("deleteProductByCode(): Could not establish connection to database.");
        }

        int deletedRows = 0;
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM products where productCode = ?")) {
            ps.setString(1, prodCode);
           deletedRows = ps.executeUpdate();

        }catch(SQLException e){
            log.error("deleteProductByCode() - The SQL query could not be prepared. \nException: {}", e.getMessage());
            throw e;
        }
        if(deletedRows == 0){
            removed = null;
        }

        return removed;
    }

    public boolean addProduct(Product p) throws SQLException{
        if(p == null){
            throw new IllegalArgumentException("Cannot add a null Product to database");
        }
        Connection conn = connector.getConnection();
        if(conn == null){
            throw new SQLException("addProduct(): Could not establish connection to database.");
        }

        int addedRows = 0;
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, p.getProductCode());
            ps.setString(2, p.getProductName());
            ps.setString(3, p.getProductLine());
            ps.setString(4, p.getProductScale());
            ps.setString(5, p.getProductVendor());
            ps.setString(6, p.getProductDescription());
            ps.setInt(7, p.getQuantityInStock());
            ps.setDouble(8, p.getBuyPrice());
            ps.setDouble(9, p.getMsrp());

            addedRows = ps.executeUpdate();

        }catch(SQLException e){
            log.error("addProduct() - The SQL query could not be prepared. \nException: {}", e.getMessage());
            throw e;
        }
        return addedRows == 1;
    }
}
