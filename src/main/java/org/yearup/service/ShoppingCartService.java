package org.yearup.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId) {

        ShoppingCart cart = new ShoppingCart();
        var cartItems = shoppingCartRepository.findByUserId(userId);
        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            cart.add(item);
        }
        return cart;
    }


    public ShoppingCart addProduct(int userId, int productId) {
        CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);
        if (existing == null) {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(1);
            shoppingCartRepository.save(newItem);
        } else {
            existing.setQuantity(existing.getQuantity() + 1);
            shoppingCartRepository.save(existing);
        }
        return getByUserId(userId);
    }

    @Transactional
    public ShoppingCart updateQuantity(int userId, int productId, int quantity) {
        CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);
        System.out.println("Found cart item: " + existing);
        System.out.println("UserId: " + userId + " ProductId: " + productId);
        if (existing != null) {
            existing.setQuantity(quantity);
            shoppingCartRepository.save(existing);
        }
        return getByUserId(userId);
    }

    public ShoppingCart clearCart(int userId) {
        shoppingCartRepository.deleteByUserId(userId);
        return getByUserId(userId);
    }
}
