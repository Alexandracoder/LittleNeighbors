package com.alexandracoder.littleneighbors.user.service;

import com.alexandracoder.littleneighbors.user.dto.UserStatusDTO;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserStatusDTO getUserStatus(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean hasFamily = user.getFamily() != null;

        boolean hasChildren = false;
        if (hasFamily && user.getFamily().getChildren() != null) {
            hasChildren = !user.getFamily().getChildren().isEmpty();
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.name())
                .collect(Collectors.toList());

        return new UserStatusDTO(
                hasFamily,
                hasChildren,
                hasFamily && hasChildren,
                user.getVerificationStatus(),
                roles
        );
    }
}
