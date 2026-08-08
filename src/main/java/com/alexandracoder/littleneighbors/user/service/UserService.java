package com.alexandracoder.littleneighbors.user.service;

import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.user.dto.UserStatusDTO;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // Import necesario
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    // OJO: antes esto era ":true" por defecto — distinto del resto del
    // código (application.yml, FamilyServiceImpl, EventServiceImpl, todos
    // usan ":false"). Esa inconsistencia hacía que, si por lo que fuera el
    // perfil activo no resolvía app.demo-mode aquí igual que en los demás
    // sitios, este método devolviera SIEMPRE "VERIFIED" sin mirar la BD
    // real — así que el frontend nunca detectaba que faltaba verificarse.
    @Value("${app.demo-mode:false}")
    private boolean isDemoMode;

    @Transactional
    public void submitVerification(String email, String idDocumentUrl, String selfieUrl) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si ya está VERIFIED o BLOCKED no dejamos que un reenvío lo mueva
        // de estado: verificado ya no hace falta repetirlo, y bloqueado no
        // se puede "colar" de vuelta enviando documentos otra vez.
        if (user.getVerificationStatus() == VerificationStatus.VERIFIED
                || user.getVerificationStatus() == VerificationStatus.BLOCKED) {
            return;
        }

        user.setIdDocumentUrl(idDocumentUrl);
        user.setSelfieUrl(selfieUrl);
        user.setVerificationStatus(VerificationStatus.PENDING_REVIEW);
        user.setRejectionReason(null);
        userRepository.save(user);
    }

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


        VerificationStatus status = isDemoMode ? VerificationStatus.VERIFIED : user.getVerificationStatus();

        return new UserStatusDTO(
                hasFamily,
                hasChildren,
                hasFamily && hasChildren,
                status,
                roles
        );
    }
}
