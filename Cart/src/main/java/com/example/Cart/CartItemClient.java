package com.example.Cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CartItem-Service",url ="http://localhost:8060/api/v1/CartItem" )       // Feignclient
public interface CartItemClient {

    @GetMapping("/{cartItem-id}")
    public CartItem getCartItem(@PathVariable("cartItem-id")Integer cartItemId);    // getting CartItems by ID to add to the Cart
    // first create CartItem(Product) in CartItem Microservice then cart will receive it and add it
}
