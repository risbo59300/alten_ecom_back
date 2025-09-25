package rca.risbo.E_com.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rca.risbo.E_com.entity.InventoryStatus;
import rca.risbo.E_com.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Optional<Product> findByCode(String code);

  List<Product> findByCategory(String category);

  List<Product> findByInventoryStatus(InventoryStatus status);

  Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Product> findByCategoryAndInventoryStatus(String category, InventoryStatus status, Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
  List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

  @Query("SELECT p FROM Product p WHERE p.rating >= :rating")
  List<Product> findByRatingGreaterThanEqual(@Param("rating") Double rating);

  @Query("SELECT DISTINCT p.category FROM Product p")
  List<String> findAllCategories();

  boolean existsByCode(String code);

}