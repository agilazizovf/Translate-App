package com.translate.app.controller;


import com.translate.app.model.dto.request.ClientAddRequest;
import com.translate.app.model.dto.request.LoginRequest;
import com.translate.app.model.dto.response.ClientAddResponse;
import com.translate.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginReq)  {
        return authService.authenticate(loginReq);
    }

    @PostMapping("/register")
    public ResponseEntity<ClientAddResponse> register(@Valid @RequestBody ClientAddRequest clientReq) {
        return authService.register(clientReq);
    }
}
