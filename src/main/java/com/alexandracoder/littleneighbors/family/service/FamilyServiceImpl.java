package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.enums.FamilyStatus;
import com.alexandracoder.littleneighbors.enums.PhotoModerationStatus;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.dto.OnboardingResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.dto.MatchResponseDetailDTO;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.security.service.JwtService;
import com.alexandracoder.littleneighbors.block.service.BlockService;
import com.alexandracoder.littleneighbors.specifications.FamilySpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.BusinessLogicException;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springdoc.core.service.GenericResponseService.setDescription;
import static org.springframework.http.ResponseEntity.status;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final FamilyMapper familyMapper;
    @Getter
    private final MatchRepository matchRepository;
    private final JwtService jwtService;;
    private final BlockService blockService;

    @org.springframework.beans.factory.annotation.Value("${app.demo-mode:false}")
    private boolean demoMode;

    // Mismo criterio que en MatchServiceImpl: admin y demo-mode se saltan
    // la verificación. Se repite aquí en vez de extraerlo a un helper
    // compartido para no tocar MatchServiceImpl ni arriesgar su
    // comportamiento ya probado — cambio mínimo y localizado.
    private boolean canBypassVerification(UserEntity user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.name().equals("ADMIN"));
        return demoMode || isAdmin;
    }

    @Override
    @Transactional
    public OnboardingResponseDTO createFamily(FamilyRequestDTO dto, String userEmail) {

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (familyRepository.existsByUser(user)) {
            throw new BusinessLogicException("User already has a family profile");
        }

        NeighborhoodEntity neighborhood = null;
        if (dto.neighborhoodId() != null) {
            neighborhood = neighborhoodRepository.findById(dto.neighborhoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found"));
        }

        FamilyEntity familyEntity = FamilyEntity.builder()
                .user(user)
                .familyName(dto.familyName())
                .description(dto.description())
                .representativeName(dto.representativeName())
                .profilePictureUrl(dto.profilePictureUrl())
                .photoModerationStatus(dto.profilePictureUrl() != null ? PhotoModerationStatus.PENDING : null)
                .status(dto.status() != null ? dto.status() : FamilyStatus.SURPRISE)
                .familyInterests(dto.familyInterests() != null ? dto.familyInterests() : new ArrayList<>())
                .neighborhood(neighborhood)
                .build();

        FamilyEntity saved = familyRepository.saveAndFlush(familyEntity);

        user.getRoles().remove(Role.USER);
        user.getRoles().add(Role.FAMILY);
        UserEntity updatedUser = userRepository.saveAndFlush(user);


        List<String> roles = updatedUser.getRoles().stream()
                .map(Role::name)
                .toList();

        Map<String, Object> claims = Map.of("roles", roles);

        String newAccessToken = jwtService.generateAccessToken(updatedUser.getEmail(), claims);
        String newRefreshToken = jwtService.generateRefreshToken(updatedUser.getEmail());

        FamilyResponseDTO familyResponse = this.familyMapper.toResponse(saved);

        return new OnboardingResponseDTO(
                familyResponse,
                newAccessToken,
                newRefreshToken
        );
    }
    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyById(Long id, String userEmail) {
        FamilyEntity entity = familyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + id));

        boolean isOwner = entity.getUser().getEmail().equals(userEmail);
        boolean isAdmin = userRepository.findByEmail(userEmail)
                .map(u -> u.getRoles().contains(Role.ADMIN))
                .orElse(false);

        // Ver el perfil de OTRA familia (con fotos y perfiles de sus
        // niños) requiere estar verificado. El propio perfil siempre se
        // puede ver, verificado o no.
        if (!isOwner && !isAdmin) {
            UserEntity currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
            if (!canBypassVerification(currentUser)
                    && currentUser.getVerificationStatus() != com.alexandracoder.littleneighbors.enums.VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("Your account must be VERIFIED to view other families' profiles.");
            }
        }

        // Cualquier otra familia mirando esta ficha (p.ej. desde el mapa)
        // no debe ver una foto todavía sin revisar por un admin.
        return (isOwner || isAdmin)
                ? this.familyMapper.toResponse(entity)
                : this.familyMapper.toPublicResponse(entity);
    }

    @Override
    @Transactional
    public FamilyResponseDTO updateFamily(Long id, FamilyRequestDTO dto, String userEmail) {
        FamilyEntity family = familyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + id));

        if (!family.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("You do not have permission to update this family");
        }

        family.setRepresentativeName(dto.representativeName());
        family.setFamilyName(dto.familyName());
        family.setDescription(dto.description());

        String previousPhotoUrl = family.getProfilePictureUrl();
        String newPhotoUrl = dto.profilePictureUrl();
        family.setProfilePictureUrl(newPhotoUrl);

        if (newPhotoUrl == null) {
            // Se quitó la foto: no hay nada que moderar.
            family.setPhotoModerationStatus(null);
        } else if (!newPhotoUrl.equals(previousPhotoUrl)) {
            // Foto nueva o distinta a la anterior: vuelve a pasar por
            // revisión aunque la anterior ya estuviera aprobada.
            family.setPhotoModerationStatus(PhotoModerationStatus.PENDING);
        }

        if (dto.neighborhoodId() != null) {
            family.setNeighborhood(neighborhoodRepository.findById(dto.neighborhoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found with id: " + dto.neighborhoodId())));
        }

        FamilyEntity updated = familyRepository.save(family);
        return this.familyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteFamily(Long id, String loggedUserEmail) {
        FamilyEntity family = familyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + id));

        UserEntity loggedUser = userRepository.findByEmail(loggedUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loggedUserEmail));

        if (loggedUser.getRoles().contains(Role.ADMIN) || family.getUser().getEmail().equals(loggedUserEmail)) {
            if (!family.getChildren().isEmpty()) {
                throw new BusinessLogicException("Cannot delete family with children profiles");
            }
            familyRepository.delete(family);
            loggedUser.getRoles().remove(Role.FAMILY);
            loggedUser.getRoles().add(Role.USER);
            userRepository.save(loggedUser);
        } else {
            throw new UnauthorizedAccessException("Not authorized");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FamilyResponseDTO> getAllFamilies(Pageable pageable) {
        return familyRepository.findAll(pageable).map(this.familyMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyResponseDTO> explorePlaymateFamilies(String userEmail, Long currentChildId, List<Long> interestIds, Integer minAge, Integer maxAge, Boolean includePregnant, boolean citywide) {

        FamilyEntity myFamily = familyRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Family not found for: " + userEmail));

        // Ver perfiles completos de otras familias (con fotos y perfiles
        // de sus niños) requiere estar verificado. El resumen del mapa
        // (getFamilyMapSummary) sigue abierto sin esta restricción.
        if (!canBypassVerification(myFamily.getUser())
                && myFamily.getUser().getVerificationStatus() != com.alexandracoder.littleneighbors.enums.VerificationStatus.VERIFIED) {
            throw new BusinessLogicException("Your account must be VERIFIED to explore other families' profiles.");
        }

        int min = minAge != null ? minAge : 0;
        int max = maxAge != null ? maxAge : 18;

        // "Toda la ciudad" = no restringir por barrio (pero seguimos dentro
        // de la misma ciudad, ya que hoy solo existe Valencia modelada).
        // Antes el frontend mandaba "scope" pero nunca llegaba al backend,
        // así que este toggle no hacía nada y el explore siempre filtraba
        // por barrio.
        Specification<FamilyEntity> spec = Specification
                .where(FamilySpecifications.fetchAll())
                .and(citywide ? null : FamilySpecifications.hasNeighborhood(myFamily.getNeighborhood().getId()))
                .and(FamilySpecifications.isNotMyFamily(myFamily.getId()))
                .and(FamilySpecifications.hasChildWithCriteria(min, max, interestIds, includePregnant));

        List<Long> blockedFamilyIds = blockService.getBlockedFamilyIdsInvolving(myFamily.getId());

        return familyRepository.findAll(spec).stream()
                .filter(f -> !blockedFamilyIds.contains(f.getId()))
                .map(this.familyMapper::toPublicResponse)
                .collect(Collectors.toList());
    }

    // Versión "resumida" para familias SIN verificar: solo coordenadas, sin
    // nombre/foto/descripción/niños. Así cualquiera puede ver cuántas
    // familias hay y más o menos dónde (para hacerse una idea del barrio),
    // sin exponer perfiles identificables hasta que esté verificada.
    @Override
    @Transactional(readOnly = true)
    public List<com.alexandracoder.littleneighbors.family.dto.FamilyMapPinDTO> getFamilyMapSummary(String userEmail, boolean citywide) {
        FamilyEntity myFamily = familyRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Family not found for: " + userEmail));

        Specification<FamilyEntity> spec = Specification
                .where(citywide ? null : FamilySpecifications.hasNeighborhood(myFamily.getNeighborhood().getId()))
                .and(FamilySpecifications.isNotMyFamily(myFamily.getId()));

        List<Long> blockedFamilyIds = blockService.getBlockedFamilyIdsInvolving(myFamily.getId());

        return familyRepository.findAll(spec).stream()
                .filter(f -> !blockedFamilyIds.contains(f.getId()))
                .filter(f -> f.getLatitude() != null && f.getLongitude() != null)
                .map(f -> new com.alexandracoder.littleneighbors.family.dto.FamilyMapPinDTO(f.getLatitude(), f.getLongitude()))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyByEmail(String email) {

        return familyRepository.findByUserEmail(email)
                .map(familyMapper::toResponse)
                .orElse(null);
    }

}