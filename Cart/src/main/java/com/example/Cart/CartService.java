package com.example.Cart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository repository;
    private final CartItemClient client;

    // creating new Cart
    public Cart createNewCart(Integer userID){
        Cart cart = new Cart();
        cart.setUserId(userID); // setting id
        return repository.save(cart);   // saving it
    }

    // get Cart by id
    public Cart getCart(Integer userID){
        return repository.findByuserId(userID);
    }

    // adding Items to the Cart
    public Cart addItemsToCart(Integer userid, Integer CartItemId) {
        Cart cart = repository.findByuserId(userid);        // we grab Cart from repository with userID
        CartItem cartItem = client.getCartItem(CartItemId); // then we get a CartItem from CartItem Microservice with id through FeignClient
        double totalprice = 0;  // price = 0
        cart.getCartItems().add(cartItem);  // we get all items
        for(CartItem item : cart.getCartItems()){
            totalprice+= item.getPrice();       // adding the price of each item to the totalprice
        }
        cart.setTotalPrice(totalprice);
        return repository.save(cart);   // saving it
    }
}
