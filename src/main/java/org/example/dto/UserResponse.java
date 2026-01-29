package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    // Only basic fields
    private Long id;
    private String username;
    private String email;
    private String role;
    private Integer level;
    private Long experience;
    private Long gold;
    private String currentLocation;
    private LocalDateTime createdAt;
}