package rca.risbo.E_com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rca.risbo.E_com.entity.InventoryStatus;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.exceptions.ProductNotFoundException;
import rca.risbo.E_com.services.ProductService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setCode("PROD001");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setCategory("Electronics");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setQuantity(50);
        testProduct.setInventoryStatus(InventoryStatus.INSTOCK);
        testProduct.setRating(4.5);
        testProduct.setCreatedAt(System.currentTimeMillis());
        testProduct.setUpdatedAt(System.currentTimeMillis());
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("PROD001"))
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_ExistingId_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("PROD001"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getProductById_NonExistingId_ShouldReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException("Produit non trouvé avec l'ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produit non trouvé avec l'ID: 999"));

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void createProduct_ValidProduct_ShouldCreateProduct() throws Exception {
        // Given
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("PROD001"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void createProduct_InvalidProduct_ShouldReturnBadRequest() throws Exception {
        // Given
        Product invalidProduct = new Product();
        invalidProduct.setCode(""); // Code vide - invalide

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_ValidUpdate_ShouldUpdateProduct() throws Exception {
        // Given
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void deleteProduct_ExistingProduct_ShouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void searchProducts_ShouldReturnPagedResults() throws Exception {
        // Given
        Page<Product> page = new PageImpl<>(Arrays.asList(testProduct));
        when(productService.searchProductsByName(eq("Test"), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));

        verify(productService, times(1)).searchProductsByName(eq("Test"), any());
    }
}
