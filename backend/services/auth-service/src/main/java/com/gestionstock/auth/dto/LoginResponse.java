package com.gestionstock.auth.dto;

import com.gestionstock.auth.model.Role;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private String name;
    private String email;
    private Role role;
}
