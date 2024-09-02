package com.example.User.Auth;

import com.example.User.Token.TokenRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {       // Custom Logout Handler to delete Cookies with JWT and make the other JWT in DB useless

    private final TokenRepo tokenRepo;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // getting the Authheader from the Request
        String authHeader = request.getHeader("Authorization");
        String jwt = "";
        // checking for a Valid AuthHeader with a Token
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);      // assign the Token value from the AuthHeader to the JWT
        }else{
            // if no AuthHeader check the Cookies
            Cookie[] cookies = request.getCookies();
            if(cookies !=null){
                for (Cookie cookie : cookies){
                    if (cookie.getName().equals("JWT")){
                        jwt = cookie.getValue();        // if a Cookie called JWT is found get the value and assign it to the JWT
                        break;
                    }
                }
            }
        }
        Cookie oldCookie = new Cookie("JWT", null);
        oldCookie.setHttpOnly(true);
        oldCookie.setPath("/");         // delete the Old Cookie
        oldCookie.setMaxAge(0);
        response.addCookie(oldCookie);
        var storedToken = tokenRepo.findByToken(jwt).orElse(null);      // get all Tokens by the TokenName
        if(storedToken !=null){
            storedToken.setRevoked(true);
            storedToken.setExpired(true);       // and make them useless by revoking them and setting them to expired
            tokenRepo.save(storedToken);
        }
    }
}
