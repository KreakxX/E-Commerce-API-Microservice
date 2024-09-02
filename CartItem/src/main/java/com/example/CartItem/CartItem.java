package com.example.CartItem;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {         // CartItem Entity
    @Id
    @GeneratedValue
    private Integer cartItemId;

    private long amount;

    private double price;

    private String name;
}
