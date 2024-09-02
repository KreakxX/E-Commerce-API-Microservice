package com.example.Api_Gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWT_Filter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(     // public Endpoints these can be reached without JWT
            "/api/v1/User/register",
            "/api/v1/User/login",
            "/api/v1/User/logout",
            "/api/v1/User/refresh"
    );
    private static final List<String> ADMIN_ENDPOINTS = Arrays.asList("/api/v1/User/id/*");     // Admin Endpoints these can be reached with Role ADMIN

    private final JwtService service;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {        // globalfilter method
    String path = exchange.getRequest().getURI().getPath();     // getting the path of the request

    if(isPublicEndpoint(path)){     // if it's a public point we will continue without checking for JWT
        return chain.filter(exchange);
    }

    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);     // we grab the Authheader
    String token = null;    // initialize the token

    if(authHeader != null && authHeader.startsWith("Bearer ")){    // if there is an Authheader and its starts with Bearer (prefix for JWT Tokens)
        token = authHeader.substring(7);        // then token will be the substring of authheader so "Bearer " that's 6 and after this is the token
    }else{
        var jwtCookies = exchange.getRequest().getCookies().get("JWT"); // then we grab the Cookie called JWT

        if (jwtCookies != null && !jwtCookies.isEmpty()) {
            token = jwtCookies.get(0).getValue();       // and get hes Value
        }
    }

    if(token == null){      // if its null tho error
        log.error("token is null");
    }

    if(!service.isTokenValid(token)){       // then we check if the token is not valid we will send a 401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    String  userRole = service.extractRoles(token);     // grabbing Role

    if(isAdminEndpoint(path) && !userRole.equals("ADMIN")){     // if the Path is  an Admin path but the Role is no Admin role
        log.error("User have no permission do access an Admin Endpoint");   // then we log
    }

    return chain.filter(exchange);      // if it's a Valid Token or the role is Admin and there is an Admin Path then we continue

    }

    @Override
    public int getOrder() {
        return 0;
    }
    private boolean isPublicEndpoint(String path){
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::matches);
    }   // boolean for checking the PUBLIC ENDPOINTS
    private boolean isAdminEndpoint(String path){
        return ADMIN_ENDPOINTS.stream().anyMatch(path::matches);
    }    }   // boolean for checking the ADMIN ENDPOINTS


