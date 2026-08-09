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
    public void register(@Valid RegisterRequest request, Locale locale) throws UserAlreadyExistsException {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already taken: " + request.email());
        }

        // BUG DE SEGURIDAD detectado en el piloto: antes se guardaba
        // directamente con PENDING_REVIEW y no se generaba ningún token,
        // así que nadie tenía que verificar su email para usar la app.
        String verificationToken = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .roles(new HashSet<>(Set.of(Role.USER)))
                .consentGiven(true)
                .consentAt(LocalDateTime.now())
                .privacyPolicyVersion(CURRENT_PRIVACY_POLICY_VERSION)
                .verificationStatus(VerificationStatus.UNVERIFIED)
                .emailVerificationToken(verificationToken)
                .emailVerificationExpires(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken, locale);
        } catch (Exception e) {
            // Este log es la única pista visible de un fallo de envío (el
            // registro sigue devolviendo 201 aunque el correo no llegue,
            // a propósito, para no filtrar si un email ya existe). Si el
            // correo de verificación no llega pero el de "olvidé mi
            // contraseña" sí, mirar aquí primero: puede ser algo propio
            // de esta plantilla/asunto, no del SMTP en general.
            log.error("FALLO AL ENVIAR EMAIL DE VERIFICACIÓN a {} - revisar configuración SMTP " +
                            "(SPRING_MAIL_HOST/USERNAME/PASSWORD) y APP_MAIL_FROM_ADDRESS en el entorno: {}",
                    user.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email, Locale locale) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // Si ya está verificado (o más allá), no tiene sentido reenviar.
            if (user.getVerificationStatus() != VerificationStatus.UNVERIFIED) {
                return;
            }

            String verificationToken = UUID.randomUUID().toString();
            user.setEmailVerificationToken(verificationToken);
            user.setEmailVerificationExpires(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            try {
                emailService.sendVerificationEmail(user.getEmail(), verificationToken, locale);
            } catch (Exception e) {
                log.error("FALLO AL REENVIAR EMAIL DE VERIFICACIÓN a {}: {}", user.getEmail(), e.getMessage(), e);
            }
        });
    }

    @Override
    @Transactional
    public void verifyEmail(String token, Locale locale) {
        UserEntity user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token"));

        if (user.getEmailVerificationExpires() == null
                || user.getEmailVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        // Confirmar el email demuestra que controlas ese correo, nada más
        // — NO debe mover el estado de verificación de identidad. Antes
        // esto saltaba directo a PENDING_REVIEW aquí mismo, de la época en
        // que "verificar" solo era confirmar el email; con la subida real
        // de DNI+selfie (ver UserService.submitVerification), ese salto
        // automático hacía que la persona nunca llegase a ver el
        // formulario de subida (PENDING_REVIEW hace que /verify-id
        // muestre "en revisión" en vez del formulario) y el admin recibía
        // solicitudes sin ningún documento que revisar.
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpires(null);
        user.setEmailVerified(true);
        userRepository.save(user);

        // El email de bienvenida se dispara aquí, no en el registro: así
        // solo llega un correo por cada paso (verificación al registrarse,
        // bienvenida al confirmar), en vez de los dos casi seguidos de
        // antes. Si falla el envío no debe tumbar la verificación, que ya
        // se ha guardado correctamente.
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), locale);
        } catch (Exception e) {
            log.error("No se pudo enviar el email de bienvenida a {}", user.getEmail(), e);
        }
    }

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedAccessException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid credentials");
        }

        // BUG DE SEGURIDAD detectado en el piloto: login() no comprobaba
        // en absoluto que el email estuviera confirmado. Usamos
        // emailVerified (no verificationStatus): ese campo ahora es solo
        // sobre la verificación de identidad con documentos, algo
        // deliberadamente posterior al login, no un requisito para entrar.
        if (!user.isEmailVerified()) {
            throw new UnauthorizedAccessException("Please verify your email before logging in.");
        }
        if (user.getVerificationStatus() == VerificationStatus.BLOCKED) {
            throw new UnauthorizedAccessException("This account has been blocked. Contact support for more information.");
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
                family.getLongitude(),
                family.getPhotoModerationStatus(),
                family.getPhotoRejectionReason()
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
    @Transactional
    // Sin @Transactional, findByEmail() y save() corrían en sesiones
    // Hibernate distintas: el usuario llegaba "detached" a save(), forzando
    // un merge() que tiene que releer el usuario entero desde BD y
    // reinicializar sus colecciones EAGER (family + roles) desde cero. Ahí
    // es donde Hibernate tropezaba con un bug de re-entrada al inicializar
    // dos colecciones EAGER a la vez -> ConcurrentModificationException.
    // Con @Transactional, findByEmail+save comparten sesión: el usuario ya
    // está "managed" y save() no necesita merge() en absoluto.
    public void initiatePasswordReset(String email, Locale locale) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordExpires(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            emailService.sendResetPasswordEmail(user.getEmail(), token, locale);
        }, () -> log.debug(
                "Password reset requested for an email with no matching account: {}. " +
                "No email sent (esto es esperado y no se muestra al usuario, por seguridad; " +
                "OJO: findByEmail es sensible a mayúsculas/minúsculas, revisar si el email " +
                "de prueba coincide exactamente con el registrado).",
                email
        ));
    }

    @Override
    @Transactional
    // Mismo bug que en initiatePasswordReset: sin @Transactional,
    // findByResetPasswordToken() y save() corrían en sesiones distintas,
    // forzando un merge() de entidad detached que dispara el mismo bug de
    // Hibernate con las colecciones EAGER (family + roles).
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