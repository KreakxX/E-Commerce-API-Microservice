package com.example.User.Token;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<token,Integer> {

    @Query("SELECT t FROM token t WHERE t.userId = :id AND t.expired = false AND t.revoked = false")
    List<token> findAllByValidTokensByUser(@Param("id") Integer id);

    Optional<token> findByToken(String token);
}
