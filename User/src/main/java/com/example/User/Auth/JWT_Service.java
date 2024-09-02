package com.example.User.Auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JWT_Service {
    private final String SecretKey = "F705CEC544A68D87EE24773CB0E1930C6A7C1031195F3495F2CCA033D9DB0561";        // Secret Key for Signing the Token
    private final long JWTExpiration = 86400000;        // expiration for JWT token
    private final long RefreshExpiration = 604800000;   // expiration for Refresh Token

    // Method for getting the Signing Key
    public Key getSigningKey(){
        return Keys.hmacShaKeyFor(SecretKey.getBytes());
    }

    // Method for extracting the username out of the Token
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    // Method for extracting one Claim out of all Claims like the expiration or the subject(username)
    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    // Method for extracting all Claims out of the Token (all data out of Token)
    public Claims extractAllClaims(String token) {
        return  Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Method for building the Token is important for generating the Token
    public String buildToken(UserDetails userDetails, Map<String,Object> claims, long expiration){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Method for generating the JWT token only with a User object (UserDetails)
    public String generateToken(UserDetails userDetails){
        return generateToken(userDetails,JWTExpiration,new HashMap<>());
    }

    // Method for generating JWT token but adding the role Claim to it
    public String generateToken(UserDetails userDetails,long expiration,Map<String,Object> extraClaims){
        String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("No Role Found");
        extraClaims.put("role",role);
        return buildToken(userDetails,extraClaims,expiration);
    }

    // Method for generating a Refresh Token
    public String generateRefreshToken(UserDetails userDetails){
        return generateToken(userDetails,RefreshExpiration,new HashMap<>());
    }

    // Method for checking if the Token is Valid
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return userDetails.getUsername().equals(username) && !isNotExpired(token);
    }

    // Method for checking if it's not expired by comparing the expiration date with the current Date
    public boolean isNotExpired(String token){
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }



}
