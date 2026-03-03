package com.alexandracoder.littleneighbors.user.service;

import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.user.dto.UserRegisterDTO;
import com.alexandracoder.littleneighbors.user.dto.UserResponseDTO;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO registerUser(UserRegisterDTO dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(u -> { throw new EntityExistsException("User already exists"); });

        UserEntity user = UserEntity.builder()
                .email(dto.email())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .password(passwordEncoder.encode(dto.password()))
                .roles(Set.of(Role.USER))
                .build();

        UserEntity saved = userRepository.save(user);
        return new UserResponseDTO(saved.getId(), saved.getEmail(), saved.getFirstName(), saved.getLastName());
    }
}
