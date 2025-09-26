package rca.risbo.E_com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.entity.WishlistItem;
import rca.risbo.E_com.repositories.WishlistItemRepository;

import java.util.List;

@Service
@Transactional
public class WishlistService {

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private ProductService productService;

    public List<WishlistItem> getUserWishlist(User user) {
        return wishlistItemRepository.findByUser(user);
    }

    public WishlistItem addToWishlist(User user, Long productId) {
        Product product = productService.getProductById(productId);

        if (wishlistItemRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Le produit est déjà dans la liste d'envies");
        }

        WishlistItem item = new WishlistItem(user, product);
        return wishlistItemRepository.save(item);
    }

    public void removeFromWishlist(User user, Long productId) {
        Product product = productService.getProductById(productId);
        wishlistItemRepository.deleteByUserAndProduct(user, product);
    }

    public boolean isInWishlist(User user, Long productId) {
        Product product = productService.getProductById(productId);
        return wishlistItemRepository.existsByUserAndProduct(user, product);
    }
}
