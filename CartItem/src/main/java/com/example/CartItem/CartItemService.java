package com.example.CartItem;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository repository;

    // creating new CartItem (Product)
    public CartItem createCartItem(String name, long amount, double price){
        CartItem cartItem = new CartItem();
        cartItem.setAmount(amount);
        cartItem.setName(name);
        cartItem.setPrice(price);
        return  repository.save(cartItem);
    }

    // getting CartItem by id
    public CartItem getCartItem(Integer cartItemId) {
        return repository.findById(cartItemId).orElseThrow();
    }
}
