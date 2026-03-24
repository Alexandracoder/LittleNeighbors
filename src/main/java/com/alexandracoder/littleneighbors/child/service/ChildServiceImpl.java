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
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional(readOnly = true)
    public List<ChildResponseDTO> findAllByFamilyEmail(String email) {
        FamilyEntity family = getFamilyByUserEmail(email);
        return childRepository.findAllByFamilyId(family.getId())
                .stream()
                .map(childMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChildResponseDTO create(ChildRequestDTO dto, String username) {
        FamilyEntity family = getFamilyByUserEmail(username);

        ChildEntity child = new ChildEntity();
        child.setLifeStage(dto.lifeStage());
        child.setBirthDate(dto.birthDate());
        child.setDueDate(dto.dueDate());
        child.setGender(dto.gender());
        child.setPrenatal(dto.isPrenatal() != null && dto.isPrenatal());
        child.setFamily(family);

        updateChildInterests(child, dto.interestIds());

        ChildEntity saved = childRepository.save(child);
        return childMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ChildResponseDTO update(Long id, ChildRequestDTO dto, String username) {
        ChildEntity child = checkOwnership(id, username);

        child.setLifeStage(dto.lifeStage());
        child.setBirthDate(dto.birthDate());
        child.setGender(dto.gender());

        updateChildInterests(child, dto.interestIds());

        ChildEntity updated = childRepository.save(child);
        return childMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteByIdAndFamilyEmail(Long id, String email) {
        ChildEntity child = checkOwnership(id, email);
        childRepository.delete(child);
    }

    @Override
    @Transactional(readOnly = true)
    public ChildResponseDTO getById(Long id, String username) {
        ChildEntity child = checkOwnership(id, username);
        return childMapper.toResponseDTO(child);
    }

    // Helper para obtener la familia de forma centralizada
    private FamilyEntity getFamilyByUserEmail(String email) {
        return familyRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Family profile not found for user: " + email));
    }

    // Lógica de intereses separada para reutilización
    private void updateChildInterests(ChildEntity child, Set<Long> interestIds) {
        if (child.getInterests() == null) {
            child.setInterests(new HashSet<>());
        } else {
            child.getInterests().clear();
        }

        if (interestIds != null && !interestIds.isEmpty()) {
            List<InterestEntity> interests = interestRepository.findAllById(interestIds);
            if (interests.size() != interestIds.size()) {
                throw new ResourceNotFoundException("One or more interest IDs not found");
            }
            child.getInterests().addAll(interests);
        }
    }

    // Verificación de seguridad robusta
    private ChildEntity checkOwnership(Long childId, String username) {
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found with id: " + childId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return child;

        if (!child.getFamily().getUser().getEmail().equals(username)) {
            throw new UnauthorizedAccessException("You do not have permission to manage this child");
        }

        return child;
    }

    }