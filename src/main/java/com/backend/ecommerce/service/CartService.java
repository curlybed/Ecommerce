package com.backend.ecommerce.service;

import com.backend.ecommerce.entities.Cart;
import com.backend.ecommerce.entities.Product;
import com.backend.ecommerce.entities.User;
import com.backend.ecommerce.repositories.CartRepository;
import com.backend.ecommerce.repositories.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;


    @Transactional
    public Cart addProductToCart(String userId,Cart cart) {
        Cart userCart = cartRepository.save(cart);

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setCart(userCart);
        userRepository.save(user);

        return userCart;
    }

    @Transactional
    public Cart removeProductFromCart(String cartId, String productId) {
        Cart cart = cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));


        cart.getProducts().removeIf(product -> product.getProductId().equals(productId));

        return cartRepository.save(cart);
    }
    @Transactional
    public void clearCart(Cart cart){
         cartRepository.delete(cart);
    }

    @Transactional
    public Cart increaseQuantity(String cartId, String productId){

        Cart cart = cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product productToAdd = productService.findByProductId(productId);
        cart.getProducts().add(productToAdd);

        return cartRepository.save(cart);

    }

    @Transactional
    public Cart decreaseQuantity(String cartId, String productId){
        Cart cart = cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        Product productToDecrease = productService.findByProductId(productId);
        for (int i= 0; i<cart.getProducts().size();i++){
            if(cart.getProducts().get(i).getProductId().equals(productToDecrease.getProductId())){
                cart.getProducts().remove(i);
                break;
            }
        }

        return cartRepository.save(cart);
    }


}