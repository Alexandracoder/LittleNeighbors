package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.specifications.FamilySpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final FamilyMapper familyMapper;

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyById(Long id, String name) {
        FamilyEntity entity = familyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Family not found with id: " + id));
        return this.familyMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public FamilyResponseDTO createFamily(FamilyRequestDTO dto, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (familyRepository.existsByUser(user)) {
            throw new IllegalStateException("User already has a family");
        }

        FamilyEntity familyEntity = new FamilyEntity();
        familyEntity.setUser(user);
        familyEntity.setFamilyName(dto.familyName());
        familyEntity.setDescription(dto.description());
        familyEntity.setRepresentativeName(dto.representativeName());
        familyEntity.setProfilePictureUrl(dto.profilePictureUrl());

        if (dto.neighborhoodId() != null) {
            familyEntity.setNeighborhood(neighborhoodRepository.findById(dto.neighborhoodId())
                    .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found with ID: " + dto.neighborhoodId())));
        }
        FamilyEntity saved = familyRepository.save(familyEntity);

        user.getRoles().remove(Role.USER);
        user.getRoles().add(Role.FAMILY);
        userRepository.saveAndFlush(user);

        return this.familyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public FamilyResponseDTO updateFamily(Long id, FamilyRequestDTO dto, String userEmail) {
        FamilyEntity family = familyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Family not found with id: " + id));

        if (!family.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("You do not have permission to update this family");
        }

        family.setRepresentativeName(dto.representativeName());
        family.setFamilyName(dto.familyName());
        family.setDescription(dto.description());
        family.setProfilePictureUrl(dto.profilePictureUrl());

        if (dto.neighborhoodId() != null) {
            family.setNeighborhood(neighborhoodRepository.findById(dto.neighborhoodId())
                    .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found with id: " + dto.neighborhoodId())));
        }

        FamilyEntity updated = familyRepository.save(family);
        return this.familyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteFamily(Long id, String loggedUserEmail) {
        FamilyEntity family = familyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Family not found with id: " + id));

        UserEntity loggedUser = userRepository.findByEmail(loggedUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + loggedUserEmail));

        if (loggedUser.getRoles().contains(Role.ADMIN)) {
            familyRepository.delete(family);
            UserEntity owner = family.getUser();
            if (owner != null) {
                owner.getRoles().remove(Role.FAMILY);
                owner.getRoles().add(Role.USER);
                userRepository.save(owner);
            }
            return;
        }

        if (!family.getUser().getEmail().equals(loggedUserEmail)) {
            throw new SecurityException("You do not have permission to delete this family");
        }

        if (!family.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete family with children");
        }

        familyRepository.delete(family);
        loggedUser.getRoles().remove(Role.FAMILY);
        loggedUser.getRoles().add(Role.USER);
        userRepository.save(loggedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FamilyResponseDTO> getAllFamilies(Pageable pageable) {
        return familyRepository.findAll(pageable).map(this.familyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FamilyResponseDTO> explorePlaymateFamilies(String userEmail, List<Long> interestIds, Integer minAge, Integer maxAge) {
        FamilyEntity myFamily = familyRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Family profile not found for: " + userEmail));

        // Iniciamos la especificación directamente con el barrio
        Specification<FamilyEntity> spec = FamilySpecifications.hasNeighborhood(myFamily.getNeighborhood().getId());

        // Excluimos la familia del usuario logueado
        spec = spec.and((root, query, cb) -> cb.notEqual(root.get("id"), myFamily.getId()));

        // Filtro por intereses (ahora acepta List<Long>)
        if (interestIds != null && !interestIds.isEmpty()) {
            spec = spec.and(FamilySpecifications.hasChildWithInterest(interestIds));
        }

        // Filtro por edad
        if (minAge != null && maxAge != null) {
            spec = spec.and(FamilySpecifications.hasChildAgeBetween(minAge, maxAge));
        }

        // El repositorio con @EntityGraph hará el resto
        return familyRepository.findAll(spec).stream()
                .map(this.familyMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyByEmail(String email) {
        FamilyEntity entity = familyRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la familia para el usuario: " + email));
        return this.familyMapper.toResponse(entity);
    }

}