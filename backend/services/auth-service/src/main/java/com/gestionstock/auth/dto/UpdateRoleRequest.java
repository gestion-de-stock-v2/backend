package com.gestionstock.auth.dto;

import com.gestionstock.auth.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateRoleRequest {
    @NotNull private Role role;
}
