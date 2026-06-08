package com.backend.ecommerce.controller;

import com.backend.ecommerce.entities.Cart;
import com.backend.ecommerce.entities.Product;
import com.backend.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{userId}")
    public Cart addProductToCart(@PathVariable String userId, @RequestBody Cart cart ) {
        return cartService.addProductToCart(userId, cart);
    }

    @DeleteMapping("/remove/{cartId}/{productId}")
    public Cart removeProductFromCart(@PathVariable String cartId, @PathVariable String productId) {
        return cartService.removeProductFromCart(cartId, productId);
    }

    @DeleteMapping("/remove")
    public void clearCart(@RequestBody Cart cart) {
        cartService.clearCart(cart);
    }

    @PutMapping("/increase/{cartId}/{productId}")
    public Cart increaseQuantity(@PathVariable String cartId, @PathVariable String productId) {
        return cartService.increaseQuantity( cartId, productId);
    }

    @PutMapping("/decrease/{cartId}/{productId}")
    public Cart decreaseQuantity(@PathVariable String cartId, @PathVariable String productId){
        return cartService.decreaseQuantity( cartId, productId);
    }
}
