package web_patterns.samplespring2025.persistence;


import web_patterns.samplespring2025.entities.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDao {
    public void closeConnection();
    public List<Product> getAllProducts() throws SQLException;
    public List<Product> getAllProductsContainingKeyword(String keyword) throws SQLException;
    public Product getProductByCode(String prodCode) throws SQLException;
    public Product deleteProductByCode(String prodCode) throws SQLException;
    public boolean addProduct(Product p) throws SQLException;
}
