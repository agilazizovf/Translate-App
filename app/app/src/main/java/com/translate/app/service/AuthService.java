package com.translate.app.service;

import com.translate.app.dto.ExceptionDTO;
import com.translate.app.dto.request.ClientAddRequest;
import com.translate.app.dto.request.ClientUpdateRequest;
import com.translate.app.dto.request.LoginRequest;
import com.translate.app.dto.response.ClientAddResponse;
import com.translate.app.dto.response.ClientUpdateResponse;
import com.translate.app.dto.response.LoginResponse;
import com.translate.app.entity.Authority;
import com.translate.app.entity.Client;
import com.translate.app.entity.User;
import com.translate.app.repository.ClientRepository;
import com.translate.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;

    public ResponseEntity<?> authenticate(LoginRequest loginReq){
        log.info("Authenticate method started by: {}", loginReq.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginReq.getUsername(), loginReq.getPassword())
            );
            log.info("Authentication details: {}", authentication);
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.createToken(user);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            LoginResponse loginRes = new LoginResponse(username, token);

            log.info("User: {} logged in successfully", user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(loginRes);

        } catch (BadCredentialsException e) {
            log.error("Authentication error: {}", e.getMessage());
            ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage());
            ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
        }
    }

    public ResponseEntity<ClientAddResponse> register(ClientAddRequest clientAddRequest) {
        if (userRepository.findByUsername(clientAddRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Client client = new Client();
        client.setName(clientAddRequest.getName());
        client.setSurname(clientAddRequest.getSurname());
        client.setUsername(clientAddRequest.getUsername());
        client.setBirthday(clientAddRequest.getBirthday());
        client.setAddress(clientAddRequest.getAddress());
        client.setPhone(clientAddRequest.getPhone());
        client.setEmail(clientAddRequest.getEmail());

        clientRepository.save(client);

        User user = new User(clientAddRequest.getUsername(), passwordEncoder.encode(clientAddRequest.getPassword()));
        Authority authority = new Authority("USER");
        Set<Authority> authoritySet = Set.of(authority);
        user.setAuthorities(authoritySet);
        userRepository.save(user);

        log.info("Saved new client and user with username: {}", client.getUsername());

        // Prepare response
        ClientAddResponse response = new ClientAddResponse();
        response.setName(clientAddRequest.getName());
        response.setSurname(clientAddRequest.getSurname());
        response.setUsername(clientAddRequest.getUsername());
        response.setAuthority("USER");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information found");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
