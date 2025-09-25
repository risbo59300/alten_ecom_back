package rca.risbo.E_com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rca.risbo.E_com.entity.InventoryStatus;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.exceptions.DuplicateProductCodeException;
import rca.risbo.E_com.exceptions.ProductNotFoundException;
import rca.risbo.E_com.repositories.ProductRepository;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProductsPaginated(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new  ProductNotFoundException("Produit non trouvé avec l'ID: " + id));
    }

    public Product getProductByCode(String code) {
        return productRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException("Produit non trouvé avec le code: " + code));
    }

    public Product createProduct(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new DuplicateProductCodeException("Un produit avec le code " + product.getCode() + " existe déjà");
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = getProductById(id);

        // Vérifier que le code n'est pas utilisé par un autre produit
        if (existingProduct.getCode().equals(productDetails.getCode()) &&
                productRepository.existsByCode(productDetails.getCode())) {
            throw new DuplicateProductCodeException("Un produit avec le code " + productDetails.getCode() + " existe déjà");
        }

        existingProduct.setCode(productDetails.getCode());
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setImage(productDetails.getImage());
        existingProduct.setCategory(productDetails.getCategory());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());
        existingProduct.setInternalReference(productDetails.getInternalReference());
        existingProduct.setShellId(productDetails.getShellId());
        existingProduct.setRating(productDetails.getRating());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsByInventoryStatus(InventoryStatus status) {
        return productRepository.findByInventoryStatus(status);
    }

    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    public Product updateQuantity(Long id, Integer newQuantity) {
        Product product = getProductById(id);
        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }


}
