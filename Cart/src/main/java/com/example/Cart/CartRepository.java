package com.example.Cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    Cart findByuserId(Integer userId);  // finding the Cart by userId
}
