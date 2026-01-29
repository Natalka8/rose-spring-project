package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String message;
    private String tokenType; // ACCESS, REFRESH, RESET, VERIFICATION
    private Long userId;
    private Long expiresIn; // seconds until expiration
}