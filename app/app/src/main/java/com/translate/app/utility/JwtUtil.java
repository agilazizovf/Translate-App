package com.translate.app.utility;

import com.translate.app.dao.entity.Authority;
import com.translate.app.dao.entity.User;
import com.translate.app.dao.repository.UserRepository;
import com.translate.app.model.exception.UserNotFoundException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class JwtUtil {
    private final UserRepository userRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secret_key;
    @Value("${application.security.jwt.expiration}")
    private long accessTokenValidity;
    private static Key key;
    public JwtUtil(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Key initializeKey(){
        byte[] keyBytes;
        keyBytes = Decoders.BASE64.decode(secret_key);
        key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
    public String createToken(User user) {
        key = initializeKey();
        user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        Set<Authority> authorities = user.getAuthorities();
        List<String> roles = new ArrayList<>();
        for (Authority authority : authorities) {
            roles.add(authority.getName());
        }
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("authorities",roles);
        claimsMap.put("username", user.getUsername());
        claimsMap.put("user_id", user.getId());

        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        final JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(tokenValidity)
                .addClaims(claimsMap)
                .signWith(key, SignatureAlgorithm.HS512);
        log.info("Jwt token created for user: {}", user.getUsername());
        return jwtBuilder.compact();
    }

    private Claims parseJwtClaims(String token) {
        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            log.error("Error due to: {}", ex.getMessage());
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error due to: {}", ex.getMessage());
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String TOKEN_HEADER = "Authorization";
        String bearerToken = request.getHeader(TOKEN_HEADER);
        String TOKEN_PREFIX = "Bearer ";
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }



    public Integer getUserId(Claims claims){
        return (Integer) claims.get("user_id");
    }

    public Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (claims.containsKey("authorities")) {
            List<String> roles = (List<String>) claims.get("authorities");
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        return authorities;
    }

}

