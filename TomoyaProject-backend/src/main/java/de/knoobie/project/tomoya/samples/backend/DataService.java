package de.knoobie.project.tomoya.samples.backend;

import java.util.Collection;

import de.knoobie.project.tomoya.samples.backend.data.Category;
import de.knoobie.project.tomoya.samples.backend.data.Product;
import de.knoobie.project.tomoya.samples.backend.mock.MockDataService;

/**
 * Back-end service interface for retrieving and updating product data.
 */
public abstract class DataService {

    public abstract Collection<Product> getAllProducts();

    public abstract Collection<Category> getAllCategories();

    public abstract void updateProduct(Product p);

    public abstract void deleteProduct(int productId);

    public abstract Product getProductById(int productId);

    public static DataService get() {
        return MockDataService.getInstance();
    }

}
