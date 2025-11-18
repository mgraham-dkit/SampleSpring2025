package web_patterns.samplespring2025.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import web_patterns.samplespring2025.entities.Product;
import web_patterns.samplespring2025.persistence.Connector;
import web_patterns.samplespring2025.persistence.MySqlConnector;
import web_patterns.samplespring2025.persistence.ProductDao;
import web_patterns.samplespring2025.persistence.ProductDaoImpl;
import web_patterns.samplespring2025.services.ProductService;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products/")
public class ProductController {
    // Exception error codes for differentiation between exception issues
    private static final int DUPLICATE_KEY_ERROR_CODE = 1062;
    private static final int FOREIGN_KEY_CONSTRAINT_FAILS = 1452;

    private ProductService productService;

    public ProductController(){
        // Manual configuration for now
        Connector mySqlConnector = new MySqlConnector("properties/database.properties");
        ProductDao productDao = new ProductDaoImpl(mySqlConnector);
        this.productService = new ProductService(productDao);
    }

    @GetMapping(path="/{productCode}", produces="application/json")
    public Product getProduct(@PathVariable String productCode){
        try {
            Product p = productService.getProductByCode(productCode);
            if(p == null){
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "entity not found"
                );
            }
            return p;
        }catch (SQLException e){
            log.error("product with code \"{}\" could not be retrieved. Database error occurred: {}", productCode,
                    e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Database error"
            );
        }
    }

    @GetMapping(path="/getAllProducts", produces="application/json")
    public List<Product> getProducts(){
        try {
            return productService.getAllProducts();
        }catch (SQLException e){
            log.error("Product list could not be retrieved. Database error occurred: {}",
                    e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Database error"
            );
        }
    }

    @GetMapping(path="/getByKeyword", produces="application/json")
    public List<Product> getByKeyword(@RequestParam String keyword){
        try {
            return productService.getProductsByKeyword(keyword);
        }catch (SQLException e){
            log.error("Product list could not be retrieved. Database error occurred: {}",
                    e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Database error"
            );
        }
    }

    @PostMapping(path="/add", produces="application/json")
    public boolean addProduct(@RequestBody Product product){
        try{
            return productService.addProduct(product);
        }catch(SQLException e){
            log.error("Failed to add Product with code \"{}\". Database error occurred: {}",product.getProductCode(), e.getMessage());
            if(e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Product code already exists"
                );
            }else if(e.getErrorCode() == FOREIGN_KEY_CONSTRAINT_FAILS){
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Foreign key constraint failed."
                );
            }else{
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred."
                );
            }
        }
    }

}
