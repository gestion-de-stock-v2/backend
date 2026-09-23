package com.gestionstock.auth.controller;

import com.gestionstock.auth.dto.UpdateRoleRequest;
import com.gestionstock.auth.dto.UserDTO;
import com.gestionstock.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public UserDTO toggleActive(@PathVariable Long id) {
        return userService.toggleActive(id);
    }

    /** Unique voie d'attribution d'un role, en remplacement du champ role de l'inscription. */
    @PutMapping("/{id}/role")
    public UserDTO updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return userService.updateRole(id, request.getRole());
    }
}
