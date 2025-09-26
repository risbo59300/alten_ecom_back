package rca.risbo.E_com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rca.risbo.E_com.entity.CartItem;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.services.CartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<CartItem> items = cartService.getUserCart(user);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Authentication auth) {
        User user = (User) auth.getPrincipal();
        CartItem item = cartService.addToCart(user, productId, quantity);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable Long itemId,
            @RequestParam Integer quantity,
            Authentication auth) {
        User user = (User) auth.getPrincipal();
        CartItem updatedItem = cartService.updateCartItem(user, itemId, quantity);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        cartService.removeFromCart(user, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication auth) {
        User user = (User) auth.getPrincipal();
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getCartSummary(Authentication auth) {
        User user = (User) auth.getPrincipal();
        long itemCount = cartService.getCartItemCount(user);
        double total = cartService.getCartTotal(user);

        Map<String, Object> summary = Map.of(
                "itemCount", itemCount,
                "total", total
        );

        return ResponseEntity.ok(summary);
    }
}
