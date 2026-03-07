package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.AuthRequest;
import com.alexandracoder.littleneighbors.auth.dto.AuthResponse;
import com.alexandracoder.littleneighbors.auth.dto.RefreshRequest;
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
    @Lazy
    private final FamilyMapper familyMapper; // Ya es un Bean inyectado

    @Transactional
    public void register(RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
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
                .orElseThrow(() -> new RuntimeException("DEBUG: Email no encontrado"));

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("DEBUG: Password incorrecta");
        }

        Optional<FamilyEntity> familyOpt = familyRepository.findByUserEmail(user.getEmail());

        // CORRECCIÓN: Usamos la instancia inyectada familyMapper
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

    // ... (Métodos refresh, reloadUserToken, etc. se mantienen igual)

    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentProfile(String email) {
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<FamilyEntity> familyOpt = familyRepository.findByUserEmail(email);

        // CORRECCIÓN: Se eliminó la línea duplicada y se corrigió la llamada al mapper
        FamilyResponseDTO familyDto = familyOpt.map(this.familyMapper::toResponse).orElse(null);

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        return new UserProfileDTO(user.getEmail(), roles, familyDto);
    }

    public AuthResponse reloadUserTokenFromRefresh(String refreshToken) {
        // 1. Extraer el email del token de refresco
        String email = jwtService.extractEmail(refreshToken);

        // 2. Verificar que el usuario existe
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Preparar los claims (roles)
        List<String> rolesList = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", rolesList);

        // 4. Generar el nuevo par de tokens
        String newAccessToken = jwtService.generateAccessToken(user.getEmail(), claims);
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
