package com.example.User.Auth;


import com.example.User.Auth.JWT_Service;
import com.example.User.Token.TokenRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Filter extends OncePerRequestFilter {

    private final JWT_Service jwtService;
    private final UserDetailsService service;
    private final TokenRepo Tokenrepo;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();    // get Path
        if (requestURI.equals("/api/v1/User/register") || requestURI.equals("/api/v1/User/login")
                || requestURI.equals("/api/v1/User/logout") || requestURI.equals("/api/v1/User/refresh")) { // if the Path is a Public Endpoint
            filterChain.doFilter(request, response);    // continue without JWT validation
            return;
        }
     String authHeader = request.getHeader("Authorization");    // get Auth Header
     String token = "";
     if(authHeader != null && authHeader.startsWith("Bearer ")){
        token = authHeader.substring(7);        // if there is an AuthHeader with a Bearer Token get it and put it as value for the Token
     }else{
         Cookie[] cookies = request.getCookies();       // if there is no AuthHeader get the Cookies and search for a Cookie that matches the name JWT
         if(cookies !=null){
             for (Cookie cookie : cookies){
                 if (cookie.getName().equals("JWT")){
                     token = cookie.getValue();     // if so then get the Value and put it as name for the Token
                     break;
                 }
             }
         }
     }
         String username = jwtService.extractUsername(token);   // get Username from Token
         if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){        // if username not null and it's not been authenticated already
             var userDetails = service.loadUserByUsername(username); // get Userdetails
             var isTokenValid = Tokenrepo.findByToken(token).map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);  // check if the Token is not revoked or expired
             if(jwtService.isTokenValid(token,userDetails) && isTokenValid){    // if so
             var authentication = new UsernamePasswordAuthenticationToken(
                     userDetails,
                     null,              // make new Authentication and add it to the ContextHolder
                     userDetails.getAuthorities()
             );
             SecurityContextHolder.getContext().setAuthentication(authentication);
     }}
        filterChain.doFilter(request,response); // and then continue
}
}
