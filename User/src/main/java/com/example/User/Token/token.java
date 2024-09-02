package com.example.User.Token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class token {
    @Id
    @GeneratedValue
    private Integer id;

    private String token;

    private boolean expired;

    private boolean revoked;

    private Integer userId;
}
