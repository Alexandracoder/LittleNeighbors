package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.mapper.ChildMapper;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.interest.repository.InterestRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChildServiceImplTest {

    @Mock private ChildRepository childRepository;
    @Mock private FamilyRepository familyRepository;
    @Mock private InterestRepository interestRepository;
    @Mock private ChildMapper childMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ChildServiceImpl childService;

    private FamilyEntity family;
    private ChildEntity child;
    private ChildRequestDTO requestDTO;
    private ChildResponseDTO responseDTO;
    private final String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock de Seguridad para evitar NullPointerException en checkOwnership
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        UserEntity user = new UserEntity();
        user.setEmail(userEmail);

        family = new FamilyEntity();
        family.setId(1L);
        family.setUser(user);

        child = new ChildEntity();
        child.setId(10L);
        child.setGender(Gender.BOY);
        child.setFamily(family);
        child.setInterests(new HashSet<>());

        this.requestDTO = new ChildRequestDTO(
                LocalDate.of(2015, 1, 1),
                LifeStage.BORN,
                Gender.BOY,
                new HashSet<>(Arrays.asList(2L, 3L))
        );

        this.responseDTO = new ChildResponseDTO(10L, Gender.BOY, LocalDate.now(), 8, List.of(), 1L);
    }

    @Test
    void create_ShouldReturnChildResponseDTO() {
        when(familyRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(family));
        when(interestRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(childRepository.save(any())).thenReturn(child);
        when(childMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ChildResponseDTO result = childService.create(requestDTO, userEmail);

        assertThat(result).isNotNull();
        verify(childRepository).save(any());
    }

    @Test
    void update_ShouldReturnUpdatedChildResponseDTO() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(interestRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(childRepository.save(any())).thenReturn(child);
        when(childMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ChildResponseDTO result = childService.update(10L, requestDTO, userEmail);

        assertThat(result).isNotNull();
        verify(childRepository).save(any());
    }

    @Test
    void delete_ShouldRemoveChild() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));

        // El método en tu Service se llama deleteByIdAndFamilyEmail
        childService.deleteByIdAndFamilyEmail(10L, userEmail);

        verify(childRepository).delete(child);
    }

    @Test
    void delete_ShouldThrowWhenNotFound() {
        when(childRepository.findById(99L)).thenReturn(Optional.empty());

        // Cambiado a ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> childService.deleteByIdAndFamilyEmail(99L, userEmail));
    }

    @Test
    void getById_ShouldReturnResponseDTO() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(childMapper.toResponseDTO(child)).thenReturn(responseDTO);

        ChildResponseDTO result = childService.getById(10L, userEmail);

        assertThat(result).isNotNull();
        verify(childRepository).findById(10L);
    }

    @Test
    void update_ShouldThrowSecurityExceptionWhenNotOwner() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));

        // Cambiado a UnauthorizedAccessException
        assertThrows(UnauthorizedAccessException.class,
                () -> childService.update(10L, requestDTO, "wrong@email.com"));
    }

    @Test
    void getAllSummaries_ShouldReturnMappedList() {
        ChildSummaryDTO summary = new ChildSummaryDTO(10L, "BOY", 8, LifeStage.BORN);
        when(childRepository.findAll()).thenReturn(List.of(child));
        when(childMapper.toSummaryDTO(child)).thenReturn(summary);

        List<ChildSummaryDTO> result = childService.getAllSummaries();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
    }
}
