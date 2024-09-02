package com.example.CartItem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/CartItem")     // Controller for CartItem
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService service;

    // Method for creating new CartItem(Product)
    @PostMapping("/new/{name}/{amount}/{price}")
    public CartItem createNewCartItem(
            @PathVariable("name") String name,
            @PathVariable("amount") long amount,
            @PathVariable("price") double price
    ){
        return service.createCartItem(name,amount,price);
    }

    // Method for getting a CartItem by id used for FeignClient
    @GetMapping("/{cartItem-id}")
    public CartItem getCartItem(@PathVariable("cartItem-id")Integer cartItemId){
        return service.getCartItem(cartItemId);
    }

}
