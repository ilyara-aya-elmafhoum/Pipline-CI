package com.wesports.backend.application.dto;

public record LogoutResponse(
    String message,
    String status
) {
    public static LogoutResponse success(String message) {
        return new LogoutResponse(message, "success");
    }
    
    public static LogoutResponse error(String message) {
        return new LogoutResponse(message, "error");
    }
}
