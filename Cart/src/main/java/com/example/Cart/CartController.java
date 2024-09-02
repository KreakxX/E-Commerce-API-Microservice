package com.example.Cart;

import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Cart")     // Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    // creating new Cart for User
    @PostMapping("/new/{user-id}")
    public Cart createNewCart (@PathVariable("user-id")Integer userId){
        return service.createNewCart(userId);
    }

    // getting a Cart that matches UserId
    @GetMapping("/{user-id}")
    public Cart getCart(@PathVariable("cart-id")Integer userId){
        return service.getCart(userId);
    }

    // adding Items to the Cart
    @PostMapping("/{user-id}/add/{cartItem-id}")
    public Cart addItemsToCart(
            @PathVariable("user-id") Integer userid,
            @PathVariable("cartItem-id")Integer cartItemId
    ){
        return service.addItemsToCart(userid,cartItemId);
    }
}
