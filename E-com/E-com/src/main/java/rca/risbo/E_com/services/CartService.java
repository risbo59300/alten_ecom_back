package rca.risbo.E_com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rca.risbo.E_com.entity.CartItem;
import rca.risbo.E_com.entity.Product;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.exceptions.InsufficientStockException;
import rca.risbo.E_com.repositories.CartItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    public List<CartItem> getUserCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productService.getProductById(productId);

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException("Stock insuffisant pour le produit: " + product.getName());
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getQuantity() < newQuantity) {
                throw new InsufficientStockException("Stock insuffisant pour le produit: " + product.getName());
            }

            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(newItem);
        }
    }

    public CartItem updateCartItem(User user, Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article du panier non trouvé"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Accès non autorisé à cet article du panier");
        }

        if (item.getProduct().getQuantity() < quantity) {
            throw new InsufficientStockException("Stock insuffisant pour le produit: " + item.getProduct().getName());
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(User user, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Article du panier non trouvé"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Accès non autorisé à cet article du panier");
        }

        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    public long getCartItemCount(User user) {
        return cartItemRepository.countByUser(user);
    }

    public double getCartTotal(User user) {
        List<CartItem> items = getUserCart(user);
        return items.stream()
                .mapToDouble(item -> item.getProduct().getPrice().intValue() * item.getQuantity())
                .sum();
    }
}
