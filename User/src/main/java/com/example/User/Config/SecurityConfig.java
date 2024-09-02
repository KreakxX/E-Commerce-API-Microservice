package com.example.User.Config;

import com.example.User.Auth.Filter;
import com.example.User.Auth.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider provider;
    private final Filter filter;            // implement Beans
    private final LogoutService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.authorizeHttpRequests(request -> request.requestMatchers(
                "/api/v1/User/register",
                         "/api/v1/User/login",                  // public Endpoints
                         "/api/v1/User/logout",
                         "/api/v1/User/refresh")
                .permitAll()        // for anyone to request to
                .requestMatchers("/api/v1/User/id/**")  // ADMIn Endpoint
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated())
                .csrf(AbstractHttpConfigurer::disable)      // disable
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)        // adding custom Filter
                .authenticationProvider(provider)   // provider
                .logout(logout -> logout.logoutUrl("/api/v1/User/logout").addLogoutHandler(service).logoutSuccessHandler((request, response, authentication) -> {
                    SecurityContextHolder.clearContext();       // logout Config for custom Logout Handler and when logout is successfully
                }));

        return security.build();

    }
}
