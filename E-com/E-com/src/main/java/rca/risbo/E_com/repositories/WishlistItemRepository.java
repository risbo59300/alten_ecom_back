package rca.risbo.E_com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.entity.WishlistItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

  List<WishlistItem> findByUser(User user);
  Optional<WishlistItem> findByUserAndProduct(User user, Product product);
  boolean existsByUserAndProduct(User user, Product product);
  void deleteByUserAndProduct(User user, Product product);
}