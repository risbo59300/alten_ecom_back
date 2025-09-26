package rca.risbo.E_com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rca.risbo.E_com.entity.CartItem;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
    long countByUser(User user);
}