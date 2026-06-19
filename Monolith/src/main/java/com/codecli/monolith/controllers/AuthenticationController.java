package com.codecli.monolith.controllers;

import com.codecli.monolith.Models.SignUpRequest;
import com.codecli.monolith.Models.User;
import com.codecli.monolith.Models.UserView;
import com.codecli.monolith.components.UserMapper;
import com.codecli.monolith.dto.SignInRequest;
import com.codecli.monolith.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping("/auth/public")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    private final UserService userService;

    private final UserMapper userMapper;

    private static final String ISSUER = "example.com";
    private static final long EXPIRATION_TIME = 36000L;

    private static final int COOKIE_MAX_AGE = 900; // 15 mins
    private static final String JWT_COOKIE_NAME = "jwt-token";
    @Value("${app.cookie.domain:localhost}")
    private String cookieDomain;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UserService userService, UserMapper userMapper,PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
        this.userMapper = userMapper;
    }
    @PostMapping("login")
    public ResponseEntity<UserView> login(@RequestBody @Valid final SignInRequest request, HttpServletResponse response) {

        try {
            Authentication authentication = authenticate(request);
            final User principal = (User) authentication.getPrincipal();

            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            return buildAuthenticationResponse(authentication, principal, response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    private Authentication authenticate(SignInRequest request) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.email, request.password));
    }

    private ResponseEntity<UserView> buildAuthenticationResponse(Authentication authentication, User principal, HttpServletResponse response) {

        String scope = extractScope(authentication);
        JwtClaimsSet claims = buildClaims(principal, scope);
        String token = generateToken(claims);

        Cookie jwtCookie = createJwtCookie(token, COOKIE_MAX_AGE);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok()
                .body(userMapper.toUserView(principal));

    }

    private String extractScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(" "));
    }

    private JwtClaimsSet buildClaims(User principal, String scope) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder baseClaimsBuilder = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRATION_TIME))
                .claim("role", scope);

        return baseClaimsBuilder
                .subject(format("User,%s,%s", principal.getId(), principal.getUsername()))
                .claim("type", "User")
                .claim("uuid", principal.getId())
                .claim("email", principal.getEmail())
                .build();

    }

    private String generateToken(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Cookie createJwtCookie(String token, int maxAge){
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        if (!cookieDomain.equals("localhost")) {
            cookie.setDomain(cookieDomain);
        }
        cookie.setAttribute("SameSite", "Strict");

        return cookie;
    }
}
