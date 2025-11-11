package services;

import daos.daos.ProductDao;
import daos.entities.Product;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ProductService {
    private ProductDao productDao;

    public ProductService(ProductDao dao){
        this.productDao = dao;
    }

    public void shutdownService(){
        productDao.closeConnection();
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDao.getAllProducts();
    }

    public Product getProductByCode(String prodCode) throws SQLException{
        log.info("Product retrieval: {}", prodCode);
        return productDao.getProductByCode(prodCode);
    }

    public List<Product> getProductsByKeyword(String keyword) throws SQLException {
        if(keyword == null){
            throw new IllegalArgumentException("Cannot search for null keyword");
        }

        if(keyword.isBlank()){
            throw new IllegalArgumentException("Keyword must be provided");
        }

        log.info("Keyword search for: {}", keyword);
        return productDao.getAllProductsContainingKeyword(keyword);
    }

    public List<Product> deleteProductsByKeyword(String keyword) throws SQLException {
        List<Product> toBeDeleted = getProductsByKeyword(keyword);

        int count = 0;
        Product currentProduct = null;
        for(count = 0; count < toBeDeleted.size(); count++){
            try {
                currentProduct = toBeDeleted.get(count);
                productDao.deleteProductByCode(currentProduct.getProductCode());
            }catch(SQLException e){
                log.error("Failed to delete product with code {}", currentProduct.getProductCode());
                break;
            }
        }

        if(count != toBeDeleted.size()){
            for (int i = 0; i < count; i++) {
                productDao.addProduct(toBeDeleted.get(i));
            }

            throw new SQLException("Failed to delete product with code " + currentProduct.getProductCode() + ". " +
                    "Action rolled back - no products containing keyword " + keyword + " were deleted");

        }

        return toBeDeleted;
    }
}
