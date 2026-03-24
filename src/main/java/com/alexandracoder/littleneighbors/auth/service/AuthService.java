package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.AuthRequest;
import com.alexandracoder.littleneighbors.auth.dto.AuthResponse;
import com.alexandracoder.littleneighbors.auth.dto.RegisterRequest;
import com.alexandracoder.littleneighbors.family.dto.FamilyAuthResponseDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.security.JwtService;
import com.alexandracoder.littleneighbors.user.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.BusinessLogicException;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final FamilyRepository familyRepository;

    private final FamilyMapper familyMapper;

    @Transactional
    public void register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new BusinessLogicException("Email already in use");
        }

        UserEntity user = new UserEntity();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));

        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.USER);
        user.setRoles(defaultRoles);

        repository.save(user);
    }

    @Transactional(readOnly = true)
    public FamilyAuthResponseDTO login(AuthRequest request) {
        UserEntity user = repository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid email or password"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid email or password");
        }

        Optional<FamilyEntity> familyOpt = familyRepository.findByUserEmail(user.getEmail());
        FamilyResponseDTO familyDto = familyOpt.map(this.familyMapper::toResponse).orElse(null);

        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", rolesList);

        String access = jwtService.generateAccessToken(user.getEmail(), claims);
        String refresh = jwtService.generateRefreshToken(user.getEmail());

        return new FamilyAuthResponseDTO(familyDto, access, refresh);
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentProfile(String email) {
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        Optional<FamilyEntity> familyOpt = familyRepository.findByUserEmail(email);
        FamilyResponseDTO familyDto = familyOpt.map(this.familyMapper::toResponse).orElse(null);

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        return new UserProfileDTO(user.getEmail(), roles, familyDto);
    }

    public AuthResponse reloadUserTokenFromRefresh(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found during token refresh"));

        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", rolesList);

        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), claims);
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
