package com.example.User;
import com.example.User.Auth.*;
import com.example.User.Token.TokenRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie; // Ensure you're using the correct import
import com.example.User.Token.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final JWT_Service jwtService;
    private final TokenRepo tokenRepo;

    // Method for getting User by userId
    public User getUser(Integer userId) {
        return repository.findById(userId).orElseThrow();
    }

    // Method for getting User by username
    public User getUserByUsername(String username){
        return repository.findByUsername(username);
    }

    // Method for registering User
    public RegisterResponse register(RegisterRequest request){
        if(repository.findByUsername(request.getUsername()) == null){       // check if username is not already in DB
        var user = User.builder()
                .password(encoder.encode(request.getPassword()))
                .username(request.getUsername())            // build User with request Data
                .role(Roles.ADMIN)
                .build();
         repository.save(user);     // saving user to repo
         String accessToken = jwtService.generateToken(user);   // generate accessToken
         String refreshToken = jwtService.generateRefreshToken(user); // generate refreshToken
         saveToken(user,accessToken);   // save accessToken to TokenRepository
            return RegisterResponse.builder()
                 .accessToken(accessToken)  // build the Response
                 .refreshToken(refreshToken)
                 .build();
    }else{
            log.error(HttpStatus.UNAUTHORIZED + "The Username: " +request.getUsername() +  "is already been used");
            return null;
        }
    }
    // Method for logging in
    public AuthResponse login(AuthRequest request, HttpServletResponse response){
        try {
            manager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),          // authenticate Username and Password with  DB
                    request.getPassword()
            ));
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Authentication failed");
        }
        User user = repository.findByUsername(request.getUsername());       // get User from Repo
        String JWT = jwtService.generateToken(user);        // generate JWT
        revokeAllUserTokens(user);      // revoke all JWT that got created before
        saveToken(user, JWT);   // and save the new Token
        Cookie cookie = new Cookie("JWT", JWT); // create a Cookie for JWTl, so you don't need to pass the accessToken by Hand everyTime
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);    // max age 1 Day
        response.addCookie(cookie); // add to Response

        String RefreshToken = jwtService.generateRefreshToken(user);    // generate RefreshToken
        return AuthResponse.builder()
                .accessToken(JWT)
                .refreshToken(RefreshToken)     // build the Response
                .build();
    }

    // Method for refreshing the JWT Token
    public void refreshJWTToken(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        String username;
        String RefreshToken;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            log.error("No Authorization Header found");             // get Refresh JWT from auth Header
            return;
        }
        RefreshToken = authHeader.substring(7);     // get from Header
        username = jwtService.extractUsername(RefreshToken);        // grab username

        if(username != null){
        var user = repository.findByUsername(username);     // get User
        if(jwtService.isTokenValid(RefreshToken,user)){
            var accessToken = jwtService.generateToken(user);       // generate new AccessToken
            var authenticationResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(RefreshToken)     // build Response
                    .build();
            Cookie oldCookie = new Cookie("JWT", null);
            oldCookie.setHttpOnly(true);
            oldCookie.setPath("/");     // delete old Cookie that contains JWT accessToken
            oldCookie.setMaxAge(0);
            response.addCookie(oldCookie);

            Cookie cookie = new Cookie("JWT",accessToken);
            cookie.setPath("/");
            cookie.setHttpOnly(true);           // build new Cookie that contains JWT accessToken
            cookie.setMaxAge(86400);
            response.addCookie(cookie);
            new ObjectMapper().writeValue(response.getOutputStream(),authenticationResponse);       // build Response
        }
        }
    }

    // Method for saving Token to Repository
    public void saveToken(User user, String JWT){
        var Token = token.builder()
                .userId(user.getUserId())
                .token(JWT)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepo.save(Token);
    }

    // Method to revoke all User Tokens
    public void revokeAllUserTokens(User user){
        var storedTokens = tokenRepo.findAllByValidTokensByUser(user.getUserId());  // get all StoredTokens with the userID
        if(storedTokens.isEmpty()){
            return;
        }
        storedTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);     // set that they are expired and revoked
        });
        tokenRepo.saveAll(storedTokens);    // save them again
    }
}
