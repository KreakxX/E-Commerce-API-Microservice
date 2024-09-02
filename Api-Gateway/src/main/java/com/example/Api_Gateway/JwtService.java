package com.example.Api_Gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.Key;

@Slf4j
@Service
public class JwtService {

    private final String SecretKey = "F705CEC544A68D87EE24773CB0E1930C6A7C1031195F3495F2CCA033D9DB0561";    // secret Key for signing JWT tokens

    public Claims extractAllClaims(String token){     // extracting all Claims from a Token using Jwts
        return Jwts.parserBuilder()       // building
                .setSigningKey(getSigningKey()) // sign it
                .build()        // finish building
                .parseClaimsJws(token)  // parse the claims
                .getBody(); // and the body of the claims
    }

    public Key getSigningKey(){
        return Keys.hmacShaKeyFor(SecretKey.getBytes());        // getting the Key
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();        // extracting all Claims and the subject of the claims is here the username / or Email sometimes
    }

    public boolean isTokenValid(String token){
        // if the token is getting parsed without throwing any exception then we know the token is valid
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        }catch (Exception e){
            log.error("Invalid Token: "+ e.getMessage());       // logging for better overview
            return false;
        }
    }

    public String  extractRoles(String token) {
        Claims claims = extractAllClaims(token);    // method for extracting the roles
        return (String) claims.get("role");  // get the specific claim "role"
}
}
