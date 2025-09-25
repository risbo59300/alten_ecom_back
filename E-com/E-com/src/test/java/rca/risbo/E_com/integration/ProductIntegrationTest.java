package rca.risbo.E_com.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.repositories.ProductRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        productRepository.deleteAll();
    }

    @Test
    void createAndRetrieveProduct_ShouldWork() throws Exception {
        // Given
        Product newProduct = new Product();
        newProduct.setCode("INT001");
        newProduct.setName("Integration Test Product");
        newProduct.setDescription("Test Description");
        newProduct.setCategory("Test Category");
        newProduct.setPrice(BigDecimal.valueOf(299.99));
        newProduct.setQuantity(100);
        newProduct.setRating(4.8);

        // When - Create product
        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("INT001"))
                .andExpect(jsonPath("$.inventoryStatus").value("INSTOCK"))
                .andReturn().getResponse().getContentAsString();

        Product createdProduct = objectMapper.readValue(createResponse, Product.class);

        // Then - Retrieve product
        mockMvc.perform(get("/api/products/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdProduct.getId()))
                .andExpect(jsonPath("$.code").value("INT001"))
                .andExpect(jsonPath("$.name").value("Integration Test Product"));
    }

    @Test
    void updateProduct_ShouldPersistChanges() throws Exception {
        // Given - Create initial product
        Product initialProduct = new Product();
        initialProduct.setCode("UPD001");
        initialProduct.setName("Original Name");
        initialProduct.setCategory("Original Category");
        initialProduct.setPrice(BigDecimal.valueOf(100.0));
        initialProduct.setQuantity(50);

        Product savedProduct = productRepository.save(initialProduct);

        // When - Update product
        savedProduct.setName("Updated Name");
        savedProduct.setPrice(BigDecimal.valueOf(150.0));
        savedProduct.setQuantity(2); // Should become LOWSTOCK

        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(150.0))
                .andExpect(jsonPath("$.inventoryStatus").value("LOWSTOCK"));

        // Then - Verify persistence
        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(150.0));
    }

    @Test
    void searchProducts_ShouldReturnMatchingResults() throws Exception {
        // Given - Create test products
        Product product1 = createTestProduct("SEARCH001", "Smartphone", "Electronics");
        Product product2 = createTestProduct("SEARCH002", "Smart TV", "Electronics");
        Product product3 = createTestProduct("SEARCH003", "Book", "Literature");

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // When & Then - Search by name
        mockMvc.perform(get("/api/products/search")
                        .param("name", "Smart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name",
                        containsInAnyOrder("Smartphone", "Smart TV")));
    }

    @Test
    void getProductsByCategory_ShouldFilterCorrectly() throws Exception {
        // Given
        Product electronics1 = createTestProduct("CAT001", "Laptop", "Electronics");
        Product electronics2 = createTestProduct("CAT002", "Mouse", "Electronics");
        Product book = createTestProduct("CAT003", "Novel", "Books");

        productRepository.save(electronics1);
        productRepository.save(electronics2);
        productRepository.save(book);

        // When & Then
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].category", everyItem(equalTo("Electronics"))));
    }

    @Test
    void deleteProduct_ShouldRemoveFromDatabase() throws Exception {
        // Given
        Product productToDelete = createTestProduct("DEL001", "To Delete", "Test");
        Product savedProduct = productRepository.save(productToDelete);

        // When - Delete
        mockMvc.perform(delete("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNoContent());

        // Then - Verify deletion
        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void paginationAndSorting_ShouldWork() throws Exception {
        // Given - Create multiple products
        for (int i = 1; i <= 15; i++) {
            Product product = createTestProduct("PAGE" + String.format("%03d", i),
                    "Product " + i, "Category");
            product.setPrice(BigDecimal.valueOf((double) (i * 10)));
            productRepository.save(product);
        }

        // When & Then - Test pagination
        mockMvc.perform(get("/api/products/paginated")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "price")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content[0].price").value(10.0))
                .andExpect(jsonPath("$.content[4].price").value(50.0));
    }

    private Product createTestProduct(String code, String name, String category) {
        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setQuantity(10);
        product.setDescription("Test description for " + name);
        return product;
    }
}
