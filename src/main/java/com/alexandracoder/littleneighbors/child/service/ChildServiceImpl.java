package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.mapper.ChildMapper;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import com.alexandracoder.littleneighbors.interest.repository.InterestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildServiceImpl implements ChildService {

    private final ChildRepository childRepository;
    private final FamilyRepository familyRepository;
    private final InterestRepository interestRepository;
    private final ChildMapper childMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ChildSummaryDTO> getAllSummaries() {
        return childRepository.findAll()
                .stream()
                .map(childMapper::toSummaryDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChildResponseDTO> findAllByFamilyEmail(String email) {

        FamilyEntity family = getFamilyByUserEmail(email);

        return childRepository.findAllByFamilyId(family.getId())
                .stream()
                .map(childMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChildResponseDTO create(ChildRequestDTO dto, String username) {
        FamilyEntity family = getFamilyByUserEmail(username);

        ChildEntity child = new ChildEntity();
        child.setBirthDate(dto.birthDate());
        child.setGender(dto.gender());
        child.setFamily(family);

        updateChildInterests(child, dto.interestIds());

        ChildEntity saved = childRepository.save(child);
        return childMapper.toResponseDTO(saved);
    }

    @Override
    public ChildResponseDTO update(Long id, ChildRequestDTO dto, String username) {
        ChildEntity child = checkOwnership(id, username);

        child.setBirthDate(dto.birthDate());
        child.setGender(dto.gender());

        updateChildInterests(child, dto.interestIds());

        ChildEntity updated = childRepository.save(child);
        return childMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id, String username) {
        ChildEntity child = checkOwnership(id, username);
        childRepository.delete(child);
    }

    @Override
    @Transactional(readOnly = true)
    public ChildResponseDTO getById(Long id, String username) {
        ChildEntity child = checkOwnership(id, username);
        return childMapper.toResponseDTO(child);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilyEntity getFamilyByUserEmail(String email) {
        return familyRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Family not found for user: " + email));
    }

    @Override
    public void deleteByIdAndFamilyEmail(Long id, String email) {
        // 1. Verificamos que el niño existe Y que pertenece a la familia que hace la petición
        // Usamos el método que ya tienes definido para reutilizar la lógica de seguridad
        ChildEntity child = checkOwnership(id, email);

        // 2. Si checkOwnership no lanzó una excepción (AccessDenied o EntityNotFound), procedemos
        childRepository.delete(child);
    }
    private void updateChildInterests(ChildEntity child, Set<Long> interestIds) {
        if (child.getInterests() == null) {
            child.setInterests(new HashSet<>());
        } else {
            child.getInterests().clear();
        }

        if (interestIds != null && !interestIds.isEmpty()) {
            List<InterestEntity> interests = interestRepository.findAllById(interestIds);
            child.getInterests().addAll(interests);
        }
    }

    private ChildEntity checkOwnership(Long childId, String username) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + childId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return child;

        FamilyEntity family = child.getFamily();
        if (family == null || family.getUser() == null || !family.getUser().getEmail().equals(username)) {
            throw new AccessDeniedException("You do not have permission to access this child's profile");
        }

        return child;
    }
}
