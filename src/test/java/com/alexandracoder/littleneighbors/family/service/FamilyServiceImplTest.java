package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FamilyServiceImplTest {

    private FamilyRepository familyRepository;
    private UserRepository userRepository;
    private NeighborhoodRepository neighborhoodRepository;
    private FamilyServiceImpl familyService;

    private UserEntity user;
    private NeighborhoodEntity neighborhood;

    @BeforeEach
    void setUp() {
        familyRepository = mock(FamilyRepository.class);
        userRepository = mock(UserRepository.class);
        neighborhoodRepository = mock(NeighborhoodRepository.class);
        familyService = new FamilyServiceImpl(familyRepository, userRepository, neighborhoodRepository);

        user = UserEntity.builder()
                .id(1L)
                .email("user@example.com")
                .roles(Set.of(Role.FAMILY))
                .build();

        neighborhood = NeighborhoodEntity.builder()
                .id(1L)
                .name("Neighborhood A")
                .build();
    }

    @Test
    void createFamily_success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(neighborhoodRepository.findById(1L)).thenReturn(Optional.of(neighborhood));
        when(familyRepository.existsByUser(user)).thenReturn(false);

        FamilyRequestDTO request = new FamilyRequestDTO(
                "1",
                "Rep Name",
                "Family Name",
                "Description",
                null,
                1L
        );

        when(familyRepository.save(any(FamilyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FamilyResponseDTO response = familyService.createFamily(request, "user@example.com");

        assertEquals("Rep Name", response.representativeName());
        assertEquals("Family Name", response.familyName());
        assertEquals("Description", response.description());
        assertEquals("Neighborhood A", response.neighborhoodName());

        ArgumentCaptor<FamilyEntity> captor = ArgumentCaptor.forClass(FamilyEntity.class);
        verify(familyRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
    }

    @Test
    void getFamilyById_notFound_throwsException() {
        when(familyRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> familyService.getFamilyById(1L, "user@example.com"));
    }

    @Test
    void updateFamily_success() {
        FamilyEntity family = new FamilyEntity();
        family.setId(1L);
        family.setUser(user);

        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(neighborhoodRepository.findById(1L)).thenReturn(Optional.of(neighborhood));
        when(familyRepository.save(any(FamilyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FamilyRequestDTO request = new FamilyRequestDTO(
                "1",
                "Updated Rep",
                "Updated Family",
                "Updated Description",
                null,
                1L
        );

        FamilyResponseDTO response = familyService.updateFamily(1L, request, "user@example.com");

        assertEquals("Updated Rep", response.representativeName());
        assertEquals("Updated Family", response.familyName());
    }

    @Test
    void deleteFamily_asAdmin_success() {
        FamilyEntity family = new FamilyEntity();
        family.setId(1L);

        UserEntity admin = UserEntity.builder()
                .id(2L)
                .email("admin@example.com")
                .roles(Set.of(Role.ADMIN))
                .build();

        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertDoesNotThrow(() -> familyService.deleteFamily(1L, "admin@example.com"));
        verify(familyRepository).delete(family);
    }

    @Test
    void deleteFamily_asOwner_withChildren_throwsException() {
        FamilyEntity family = new FamilyEntity();
        family.setId(1L);
        family.setUser(user);
        family.getChildren().add(mock(com.alexandracoder.littleneighbors.child.entity.ChildEntity.class));

        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> familyService.deleteFamily(1L, "user@example.com"));
    }

    @Test
    void testGetAllFamilies() {
        FamilyEntity family1 = new FamilyEntity();
        family1.setId(1L);
        FamilyEntity family2 = new FamilyEntity();
        family2.setId(2L);

        List<FamilyEntity> entities = List.of(family1, family2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        when(familyRepository.findAll(pageable)).thenReturn(new PageImpl<>(entities, pageable, entities.size()));

        Page<FamilyResponseDTO> result = familyService.getAllFamilies(pageable);

        assertEquals(2, result.getTotalElements());
        verify(familyRepository, times(1)).findAll(pageable);
    }
}


