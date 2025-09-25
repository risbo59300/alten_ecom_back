package rca.risbo.E_com.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import rca.risbo.E_com.entity.InventoryStatus;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product();
        testProduct1.setCode("REPO001");
        testProduct1.setName("Repository Test Product 1");
        testProduct1.setCategory("Electronics");
        testProduct1.setPrice(BigDecimal.valueOf(99.99));
        testProduct1.setQuantity(50);
        testProduct1.setInventoryStatus(InventoryStatus.INSTOCK);
        testProduct1.setRating(4.5);
        testProduct1.setCreatedAt(System.currentTimeMillis());
        testProduct1.setUpdatedAt(System.currentTimeMillis());

        testProduct2 = new Product();
        testProduct2.setCode("REPO002");
        testProduct2.setName("Repository Test Product 2");
        testProduct2.setCategory("Books");
        testProduct2.setPrice(BigDecimal.valueOf(19.99));
        testProduct2.setQuantity(5);
        testProduct2.setInventoryStatus(InventoryStatus.LOWSTOCK);
        testProduct2.setRating(3.8);
        testProduct2.setCreatedAt(System.currentTimeMillis());
        testProduct2.setUpdatedAt(System.currentTimeMillis());

        testProduct3 = new Product();
        testProduct3.setCode("REPO003");
        testProduct3.setName("Out of Stock Product");
        testProduct3.setCategory("Electronics");
        testProduct3.setPrice(BigDecimal.valueOf(299.99));
        testProduct3.setQuantity(0);
        testProduct3.setInventoryStatus(InventoryStatus.OUTOFSTOCK);
        testProduct3.setRating(4.2);
        testProduct3.setCreatedAt(System.currentTimeMillis());
        testProduct3.setUpdatedAt(System.currentTimeMillis());

        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(testProduct2);
        entityManager.persistAndFlush(testProduct3);
    }

    @Test
    void findByCode_ExistingCode_ShouldReturnProduct() {
        // When
        Optional<Product> result = productRepository.findByCode("REPO001");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Repository Test Product 1", result.get().getName());
    }

    @Test
    void findByCode_NonExistingCode_ShouldReturnEmpty() {
        // When
        Optional<Product> result = productRepository.findByCode("NONEXISTENT");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByCategory_ShouldReturnProductsInCategory() {
        // When
        List<Product> electronics = productRepository.findByCategory("Electronics");

        // Then
        assertEquals(2, electronics.size());
        assertTrue(electronics.stream()
                .allMatch(p -> "Electronics".equals(p.getCategory())));
    }

    @Test
    void findByInventoryStatus_ShouldReturnProductsWithStatus() {
        // When
        List<Product> instockProducts = productRepository.findByInventoryStatus(InventoryStatus.INSTOCK);
        List<Product> lowstockProducts = productRepository.findByInventoryStatus(InventoryStatus.LOWSTOCK);
        List<Product> outstockProducts = productRepository.findByInventoryStatus(InventoryStatus.OUTOFSTOCK);

        // Then
        assertEquals(1, instockProducts.size());
        assertEquals(1, lowstockProducts.size());
        assertEquals(1, outstockProducts.size());
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingProducts() {
        // When
        Page<Product> results = productRepository.findByNameContainingIgnoreCase(
                "repository", PageRequest.of(0, 10));

        // Then
        assertEquals(2, results.getContent().size());
        assertTrue(results.getContent().stream()
                .allMatch(p -> p.getName().toLowerCase().contains("repository")));
    }

    @Test
    void findByPriceRange_ShouldReturnProductsInRange() {
        // When
        List<Product> productsInRange = productRepository.findByPriceRange(15.0, 100.0);

        // Then
        assertEquals(2, productsInRange.size());
        assertTrue(productsInRange.stream()
                .allMatch(p -> p.getPrice().intValue() >= 15.0 && p.getPrice().intValue() <= 100.0));
    }

    @Test
    void findByRatingGreaterThanEqual_ShouldReturnQualityProducts() {
        // When
        List<Product> qualityProducts = productRepository.findByRatingGreaterThanEqual(4.0);

        // Then
        assertEquals(2, qualityProducts.size());
        assertTrue(qualityProducts.stream()
                .allMatch(p -> p.getRating() >= 4.0));
    }

    @Test
    void findAllCategories_ShouldReturnUniqueCategories() {
        // When
        List<String> categories = productRepository.findAllCategories();

        // Then
        assertEquals(2, categories.size());
        assertTrue(categories.contains("Electronics"));
        assertTrue(categories.contains("Books"));
    }

    @Test
    void existsByCode_ExistingCode_ShouldReturnTrue() {
        // When
        boolean exists = productRepository.existsByCode("REPO001");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByCode_NonExistingCode_ShouldReturnFalse() {
        // When
        boolean exists = productRepository.existsByCode("NONEXISTENT");

        // Then
        assertFalse(exists);
    }
}
