package com.example.Cart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {     // Cart Entity

    @Id
    @GeneratedValue

    private Integer cartId;     // id

    private Integer userId; // id of User to see which cart belongs to what user

    @ElementCollection
    private List<CartItem> CartItems = new ArrayList<>();   // List of CartItems (items in Cart(products)

    private double totalPrice;      // price

}
