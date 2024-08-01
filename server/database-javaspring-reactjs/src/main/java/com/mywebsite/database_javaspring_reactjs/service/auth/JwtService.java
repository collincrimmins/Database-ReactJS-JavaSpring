package com.mywebsite.database_javaspring_reactjs.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component
public class JwtService {
    @Autowired
    private UserService userDetailsService;

    public static String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    public Boolean checkToken(String token) {
        if (token == null) {return false;}
        if (!token.startsWith("Bearer ")) {return false;}
        
        // Get Token & Username
        token = token.substring(7);
        String username = extractEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails == null) {return false;}

        System.out.println(token);
        System.out.println(userDetails.getUsername() + " " + userDetails.getPassword());

        if (validateToken(token, userDetails)) {
            return true;
        }
        return false;
    }

    // Verify User Token is Valid
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Get Email from Token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Get Expiration from Token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if Token Expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public <T>T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
