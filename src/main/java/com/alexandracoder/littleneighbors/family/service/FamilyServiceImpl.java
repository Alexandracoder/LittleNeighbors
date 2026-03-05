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

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyById(Long id, String name) {
        FamilyEntity entity = familyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Family not found with id: " + id));
        return FamilyMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public FamilyResponseDTO createFamily(FamilyRequestDTO dto, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (familyRepository.existsByUser(user)) {
            throw new IllegalStateException("User already has a family");
        }

        // Creamos la entidad
        FamilyEntity familyEntity = new FamilyEntity();
        familyEntity.setUser(user);
        familyEntity.setFamilyName(dto.familyName());
        familyEntity.setDescription(dto.description());
        familyEntity.setRepresentativeName(dto.representativeName());
        familyEntity.setProfilePictureUrl(dto.profilePictureUrl());

        if (dto.neighborhoodId() != null) {
            familyEntity.setNeighborhood(neighborhoodRepository.findById(dto.neighborhoodId())
                    .orElseThrow(() -> new EntityNotFoundException("Neighborhood not found")));
        }

        // Guardamos la familia
        FamilyEntity saved = familyRepository.save(familyEntity);

        // ACTUALIZAMOS EL ROL DEL USUARIO
        user.getRoles().remove(Role.USER);
        user.getRoles().add(Role.FAMILY);

        // ¡ESTO ES LO MÁS IMPORTANTE!
        // Flush obliga a Hibernate a escribir en la DB ahora mismo
        userRepository.saveAndFlush(user);

        return FamilyMapper.toResponse(saved);
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
        return FamilyMapper.toResponse(updated);
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
        return familyRepository.findAll(pageable).map(FamilyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FamilyResponseDTO> explorePlaymateFamilies(String userEmail, Long interestId, Integer minAge, Integer maxAge) {

        FamilyEntity myFamily = familyRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Family profile not found for: " + userEmail));

        // 1. Empezamos con la condición base: mismo vecindario
        Specification<FamilyEntity> spec = FamilySpecifications.hasNeighborhood(myFamily.getNeighborhood().getId());

        // 2. Excluimos a nuestra propia familia (para no salir nosotros en el Dashboard)
        spec = spec.and((root, query, cb) -> cb.notEqual(root.get("id"), myFamily.getId()));

        // 3. Filtros opcionales
        if (interestId != null) {
            spec = spec.and(FamilySpecifications.hasChildWithInterest(interestId));
        }

        if (minAge != null && maxAge != null) {
            spec = spec.and(FamilySpecifications.hasChildAgeBetween(minAge, maxAge));
        }

        // 4. Ejecutamos la búsqueda
        return familyRepository.findAll(spec).stream()
                .map(FamilyMapper::toResponse)
                .collect(Collectors.toList());
    }
}
