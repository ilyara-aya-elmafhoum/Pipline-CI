package com.wesports.backend.application.port.inbound;

import com.wesports.backend.application.dto.AuthResponse;
import com.wesports.backend.application.dto.LoginRequest;
import com.wesports.backend.application.dto.LogoutResponse;

public interface LoginService {
    AuthResponse login(LoginRequest request);
    LogoutResponse logout(String refreshToken);
    AuthResponse refreshToken(String refreshToken);
}
