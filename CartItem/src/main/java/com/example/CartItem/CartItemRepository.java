package com.example.CartItem;

import org.springframework.data.jpa.repository.JpaRepository;

// Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
