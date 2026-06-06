package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.security.service.JwtService;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import com.alexandracoder.littleneighbors.shared.exceptions.UserAlreadyExistsException;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.specifications.UserSpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final com.alexandracoder.littleneighbors.qr.service.QrService qrService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void register(@Valid RegisterRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already taken: " + request.email());
        }

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();

        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid credentials");
        }

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("id", user.getId());


        return new AuthResponse(
                jwtService.generateAccessToken(user.getEmail(), claims),
                jwtService.generateRefreshToken(user.getEmail()),
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentProfile(String email) {
        UserEntity user = userRepository.findOne(UserSpecifications.hasEmailWithFullProfile(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return new UserProfileDTO(
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).toList(),
                user.getFamily() != null ? mapToFamilyDTO(user.getFamily()) : null
        );
    }

    private FamilyResponseDTO mapToFamilyDTO(FamilyEntity family) {
        NeighborhoodEntity neighborhood = family.getNeighborhood();

        Long neighborhoodId = (neighborhood != null) ? neighborhood.getId() : null;
        String neighborhoodName = (neighborhood != null && neighborhood.getName() != null) ? neighborhood.getName() : "Not assigned";
        String street = (neighborhood != null) ? neighborhood.getStreetName() : "Not assigned";
        String zip = (neighborhood != null) ? neighborhood.getPostalCode() : "N/A";
        String city = (neighborhood != null && neighborhood.getCity() != null)
                ? neighborhood.getCity().getName()
                : "Not assigned";

        return new FamilyResponseDTO(
                family.getId(),
                family.getRepresentativeName(),
                family.getFamilyName(),
                family.getDescription(),
                family.getProfilePictureUrl(),
                neighborhoodId,
                neighborhoodName,
                street,
                zip,
                city,
                new ArrayList<>(),
                family.getLatitude(),
                family.getLongitude()
        );
    }

    @Transactional
    public AuthResponse reloadUserTokenFromRefresh(String refreshToken) {

        String email = jwtService.extractEmail(refreshToken);

        UserEntity user = userRepository.findOne(UserSpecifications.hasEmailWithFullProfile(email))
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid session or User not found"));

        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("id", user.getId());

        String newAccessToken = jwtService.generateAccessToken(email, claims);

        return new AuthResponse(
                newAccessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
    }
}