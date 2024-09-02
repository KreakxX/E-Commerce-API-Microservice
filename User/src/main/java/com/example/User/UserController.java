package com.example.User;

import com.example.User.Auth.AuthRequest;
import com.example.User.Auth.AuthResponse;
import com.example.User.Auth.RegisterRequest;
import com.example.User.Auth.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/User")
public class UserController {

    private final UserService service;

    // register Method for creating JWT
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
       return service.register(request);
    }

    // login Method for validation username and password and creating Cookie for JWT
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request, HttpServletResponse response){
        return service.login(request, response);
    }
    // getting user by id is ADMIN Endpoint and you need Role for it
    @GetMapping("/id/{user-id}")
    public User getUser(@PathVariable("user-id") Integer userId){
        return service.getUser(userId);
    }

    // getting user by username
    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable("username") String username){
        return service.getUserByUsername(username);
    }
    // refreshing the JWT accessToken with RefreshToken
    @PutMapping("/refresh")
    public void refreshToken(HttpServletRequest request,HttpServletResponse response) throws IOException {
        service.refreshJWTToken(request, response);
    }
}
