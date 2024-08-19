package com.translate.app.service;

import com.translate.app.model.dto.request.ClientAddRequest;
import com.translate.app.model.dto.request.LoginRequest;
import com.translate.app.model.dto.response.ClientAddResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticate(LoginRequest loginReq);
    ResponseEntity<ClientAddResponse> register(ClientAddRequest clientAddRequest);
}
