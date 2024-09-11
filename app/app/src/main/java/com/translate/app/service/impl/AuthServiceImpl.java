package com.translate.app.service.impl;

import com.translate.app.model.dto.ExceptionDTO;
import com.translate.app.model.dto.request.ClientAddRequest;
import com.translate.app.model.dto.request.LoginRequest;
import com.translate.app.model.dto.response.ClientAddResponse;
import com.translate.app.model.dto.response.LoginResponse;
import com.translate.app.dao.entity.Authority;
import com.translate.app.dao.entity.Client;
import com.translate.app.dao.entity.User;
import com.translate.app.dao.repository.ClientRepository;
import com.translate.app.dao.repository.UserRepository;
import com.translate.app.model.enums.Status;
import com.translate.app.model.exception.AlreadyExistsException;
import com.translate.app.model.exception.UserNotFoundException;
import com.translate.app.service.AuthService;
import com.translate.app.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;

    @Override
    public ResponseEntity<?> authenticate(LoginRequest loginReq) {
        log.info("Authenticate method started by: {}", loginReq.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginReq.getUsername(), loginReq.getPassword())
            );
            log.info("Authentication details: {}", authentication);
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username: "+username));

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

    @Override
    public ResponseEntity<ClientAddResponse> register(ClientAddRequest clientAddRequest) {
        if (userRepository.findByUsername(clientAddRequest.getUsername()).isPresent()) {
            throw new AlreadyExistsException("Username already exists");
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
        user.setStatus(Status.ONLINE);
        userRepository.save(user);

        log.info("Saved new client and user with username: {}", client.getUsername());

        // Prepare response
        ClientAddResponse response = new ClientAddResponse();
        response.setUsername(clientAddRequest.getUsername());
        response.setStatus(Status.ONLINE);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
