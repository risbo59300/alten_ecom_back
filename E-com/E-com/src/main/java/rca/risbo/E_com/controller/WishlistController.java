package rca.risbo.E_com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rca.risbo.E_com.entity.User;
import rca.risbo.E_com.entity.WishlistItem;
import rca.risbo.E_com.services.WishlistService;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItem>> getWishlist(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<WishlistItem> items = wishlistService.getUserWishlist(user);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<WishlistItem> addToWishlist(@PathVariable Long productId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        WishlistItem item = wishlistService.addToWishlist(user, productId);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        wishlistService.removeFromWishlist(user, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> isInWishlist(@PathVariable Long productId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        boolean inWishlist = wishlistService.isInWishlist(user, productId);
        return ResponseEntity.ok(inWishlist);
    }
}
