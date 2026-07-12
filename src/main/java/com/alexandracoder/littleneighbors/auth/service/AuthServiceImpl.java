package com.alexandracoder.littleneighbors.auth.service;

import com.alexandracoder.littleneighbors.auth.dto.*;
import com.alexandracoder.littleneighbors.email.service.EmailService;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.user.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.security.service.JwtService;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import com.alexandracoder.littleneighbors.shared.exceptions.UserAlreadyExistsException;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.specifications.UserSpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String CURRENT_PRIVACY_POLICY_VERSION = "1.0";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
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
                .consentGiven(true)
                .consentAt(LocalDateTime.now())
                .privacyPolicyVersion(CURRENT_PRIVACY_POLICY_VERSION)
                .verificationStatus(VerificationStatus.PENDING_REVIEW)
                .build();

        userRepository.save(user);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentProfile(String email) {
        UserEntity user = userRepository.findOne(UserSpecifications.hasEmailWithFullProfile(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        VerificationStatus status = user.getVerificationStatus();

        VerificationStatus VerificationStatus = com.alexandracoder.littleneighbors.enums.VerificationStatus.VERIFIED;
        return new UserProfileDTO(
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).toList(),
                user.getFamily() != null ? mapToFamilyDTO(user.getFamily()) : null,
                VerificationStatus
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

    @Override
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

    @Override
    public void sendWelcomeEmail(String email, String firstName, Locale locale) {
        emailService.sendWelcomeEmail(email, firstName, locale);
    }

    @Override
    public void initiatePasswordReset(String email, Locale locale) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordExpires(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            emailService.sendResetPasswordEmail(user.getEmail(), token, locale);
        });
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        UserEntity user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getResetPasswordExpires() == null ||
                user.getResetPasswordExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        userRepository.save(user);
    }
}