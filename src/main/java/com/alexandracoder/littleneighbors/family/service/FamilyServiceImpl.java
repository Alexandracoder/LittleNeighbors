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
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.BusinessLogicException;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
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
    @Transactional
    public FamilyResponseDTO createFamily(FamilyRequestDTO dto, String userEmail) {

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (familyRepository.existsByUser(user)) {
            throw new BusinessLogicException("User already has a family profile");
        }

        FamilyEntity familyEntity = new FamilyEntity();
        familyEntity.setUser(user);
        familyEntity.setFamilyName(dto.familyName());
        familyEntity.setDescription(dto.description());
        familyEntity.setRepresentativeName(dto.representativeName());
        familyEntity.setProfilePictureUrl(dto.profilePictureUrl());

        if (dto.neighborhoodId() != null) {
            familyEntity.setNeighborhood(neighborhoodRepository.findById(dto.neighborhoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found")));
        }

        FamilyEntity saved = familyRepository.save(familyEntity);

        user.getRoles().remove(Role.USER);
        user.getRoles().add(Role.FAMILY);
        userRepository.save(user);

        return this.familyMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyById(Long id, String name) {
        FamilyEntity entity = familyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + id));
        return this.familyMapper.toResponse(entity);
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
        family.setProfilePictureUrl(dto.profilePictureUrl());

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
    public List<FamilyResponseDTO> explorePlaymateFamilies(
            String userEmail,
            Long currentChildId,
            List<Long> interestIds,
            Integer minAge,
            Integer maxAge
    ) {

        FamilyEntity myFamily = familyRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Family profile not found"));

        if (myFamily.getNeighborhood() == null) {
            throw new BusinessLogicException("You must assign a neighborhood to explore");
        }

        Long myNeighborhoodId = myFamily.getNeighborhood().getId();
        Long myFamilyId = myFamily.getId();

        Specification<FamilyEntity> spec = Specification
                .where(FamilySpecifications.hasNeighborhood(myNeighborhoodId))
                .and((root, query, cb) -> cb.notEqual(root.get("id"), myFamilyId));

        if (currentChildId != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                return cb.notEqual(root.join("children").get("id"), currentChildId);
            });
        }

        if ((interestIds != null && !interestIds.isEmpty()) || (minAge != null && maxAge != null)) {
            int effectiveMin = (minAge != null) ? minAge : 0;
            int effectiveMax = (maxAge != null) ? maxAge : 18;
            spec = spec.and(FamilySpecifications.hasChildWithCriteria(effectiveMin, effectiveMax, interestIds));
        }

        return familyRepository.findAll(spec).stream()
                .map(this.familyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyResponseDTO getFamilyByEmail(String email) {

        return familyRepository.findByUserEmail(email)
                .map(familyMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found"));
    }
}