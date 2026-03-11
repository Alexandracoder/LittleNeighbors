package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyServiceImplTest {

    @Mock private FamilyRepository familyRepository;
    @Mock private UserRepository userRepository;
    @Mock private NeighborhoodRepository neighborhoodRepository;
    @Mock private FamilyMapper familyMapper;

    @InjectMocks
    private FamilyServiceImpl familyService;

    private UserEntity user;
    private NeighborhoodEntity neighborhood;
    private FamilyEntity familyEntity;

    @BeforeEach
    void setUp() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .roles(roles)
                .build();

        neighborhood = NeighborhoodEntity.builder()
                .id(1L)
                .name("Neighborhood A")
                .build();

        familyEntity = new FamilyEntity();
        familyEntity.setId(1L);
        familyEntity.setUser(user);
        familyEntity.setNeighborhood(neighborhood);
        familyEntity.setChildren(new ArrayList<>());
    }

    @Test
    void createFamily_success() {
        // Orden real: userId, representativeName, familyName, description, profilePictureUrl, neighborhoodName
        FamilyRequestDTO request = new FamilyRequestDTO("1", "Rep Name", "Family Name", "Description", "url", 1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(neighborhoodRepository.findById(1L)).thenReturn(Optional.of(neighborhood));
        when(familyRepository.existsByUser(user)).thenReturn(false);
        when(familyRepository.save(any(FamilyEntity.class))).thenReturn(familyEntity);

        // Orden real: id, representativeName, familyName, description, profilePictureUrl, neighborhoodName, street, postal, city, children
        when(familyMapper.toResponse(any(FamilyEntity.class)))
                .thenReturn(new FamilyResponseDTO(1L, "Rep Name", "Family Name", "Description", "url", "Nbhd", "St", "123", "City", new ArrayList<>()));

        FamilyResponseDTO response = familyService.createFamily(request, "user@example.com");

        assertNotNull(response);
        assertEquals("Rep Name", response.representativeName());
    }

    @Test
    void getFamilyByEmail_success() {
        when(familyRepository.findByUserEmail("user@example.com")).thenReturn(Optional.of(familyEntity));
        when(familyMapper.toResponse(any(FamilyEntity.class)))
                .thenReturn(new FamilyResponseDTO(1L, "Rep", "Fam", "Desc", "url", "Nbhd", "St", "123", "City", new ArrayList<>()));

        FamilyResponseDTO result = familyService.getFamilyByEmail("user@example.com");

        assertNotNull(result);
        verify(familyRepository).findByUserEmail("user@example.com");
    }

    @Test
    void updateFamily_success() {
        // Orden real: userId, representativeName, familyName, description, profilePictureUrl, neighborhoodName
        FamilyRequestDTO request = new FamilyRequestDTO("1", "Updated Rep", "Updated Family", "Updated Desc", "url", 1L);

        when(familyRepository.findById(1L)).thenReturn(Optional.of(familyEntity));
        when(neighborhoodRepository.findById(1L)).thenReturn(Optional.of(neighborhood));
        when(familyRepository.save(any(FamilyEntity.class))).thenReturn(familyEntity);
        when(familyMapper.toResponse(any(FamilyEntity.class)))
                .thenReturn(new FamilyResponseDTO(1L, "Updated Rep", "Updated Family", "Updated Desc", "url", "Nbhd", "St", "123", "City", new ArrayList<>()));

        FamilyResponseDTO response = familyService.updateFamily(1L, request, "user@example.com");

        assertEquals("Updated Rep", response.representativeName());
    }

    @Test
    void explorePlaymateFamilies_success() {
        String userEmail = "user@example.com";
        when(familyRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(familyEntity));

        List<FamilyEntity> foundFamilies = List.of(familyEntity);
        when(familyRepository.findAll(any(Specification.class))).thenReturn(foundFamilies);

        // Mock del mapper devolviendo un DTO con 10 argumentos
        when(familyMapper.toResponse(any(FamilyEntity.class)))
                .thenReturn(new FamilyResponseDTO(1L, "Rep", "Fam", "Desc", "url", "Nbhd", "St", "123", "City", new ArrayList<>()));

        List<FamilyResponseDTO> results = familyService.explorePlaymateFamilies(userEmail, null, 2, 5);

        assertFalse(results.isEmpty());
    }

    @Test
    void deleteFamily_owner_noChildren_success() {
        when(familyRepository.findById(1L)).thenReturn(Optional.of(familyEntity));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> familyService.deleteFamily(1L, "user@example.com"));
        verify(familyRepository).delete(familyEntity);
    }
}

