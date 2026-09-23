package com.gestionstock.auth.service;

import com.gestionstock.auth.dto.UserDTO;
import com.gestionstock.auth.exception.ResourceNotFoundException;
import com.gestionstock.auth.model.Role;
import com.gestionstock.auth.model.User;
import com.gestionstock.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(UserService::toDTO).toList();
    }

    public UserDTO findById(Long id) {
        return toDTO(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id)));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable : " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDTO toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
        user.setActive(!Boolean.TRUE.equals(user.getActive()));
        return toDTO(userRepository.save(user));
    }

    /** Seule voie d'attribution d'un role : reservee aux ADMIN par le controleur. */
    @Transactional
    public UserDTO updateRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
        user.setRole(role);
        return toDTO(userRepository.save(user));
    }

    public static UserDTO toDTO(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .active(u.getActive())
                .build();
    }
}
