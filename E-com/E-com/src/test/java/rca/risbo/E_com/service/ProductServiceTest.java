package rca.risbo.E_com.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rca.risbo.E_com.entity.InventoryStatus;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.exceptions.DuplicateProductCodeException;
import rca.risbo.E_com.exceptions.ProductNotFoundException;
import rca.risbo.E_com.repositories.ProductRepository;
import rca.risbo.E_com.services.ProductService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Product testProduct2;

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

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setCode("PROD002");
        testProduct2.setName("Test Product 2");
        testProduct2.setCategory("Books");
        testProduct2.setPrice(BigDecimal.valueOf(19.99));
        testProduct2.setQuantity(5);
        testProduct2.setInventoryStatus(InventoryStatus.LOWSTOCK);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct, testProduct2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getAllProducts();

        // Then
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProductsPaginated_ShouldReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct, testProduct2);
        Page<Product> expectedPage = new PageImpl<>(products, pageable, products.size());
        when(productRepository.findAll(pageable)).thenReturn(expectedPage);

        // When
        Page<Product> actualPage = productService.getAllProductsPaginated(pageable);

        // Then
        assertEquals(2, actualPage.getContent().size());
        assertEquals(expectedPage, actualPage);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void getProductById_ExistingId_ShouldReturnProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Product actualProduct = productService.getProductById(1L);

        // Then
        assertEquals(testProduct, actualProduct);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_NonExistingId_ShouldThrowException() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999L)
        );
        assertEquals("Produit non trouvé avec l'ID: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void createProduct_ValidProduct_ShouldCreateProduct() {
        // Given
        when(productRepository.existsByCode("PROD001")).thenReturn(false);
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        Product createdProduct = productService.createProduct(testProduct);

        // Then
        assertEquals(testProduct, createdProduct);
        verify(productRepository, times(1)).existsByCode("PROD001");
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void createProduct_DuplicateCode_ShouldThrowException() {
        // Given
        when(productRepository.existsByCode("PROD001")).thenReturn(true);

        // When & Then
        DuplicateProductCodeException exception = assertThrows(
                DuplicateProductCodeException.class,
                () -> productService.createProduct(testProduct)
        );
        assertEquals("Un produit avec le code PROD001 existe déjà", exception.getMessage());
        verify(productRepository, times(1)).existsByCode("PROD001");
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_ValidUpdate_ShouldUpdateProduct() {
        // Given
        Product updatedDetails = new Product();
        updatedDetails.setCode("PROD001");
        updatedDetails.setName("Updated Product");
        updatedDetails.setPrice(BigDecimal.valueOf(149.99));
        updatedDetails.setQuantity(75);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsByCode("PROD001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.updateProduct(1L, updatedDetails);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_ExistingProduct_ShouldDeleteProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        // Given
        List<Product> electronicsProducts = Arrays.asList(testProduct);
        when(productRepository.findByCategory("Electronics")).thenReturn(electronicsProducts);

        // When
        List<Product> result = productService.getProductsByCategory("Electronics");

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    void updateQuantity_ShouldUpdateQuantityAndStatus() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.updateQuantity(1L, 5);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
