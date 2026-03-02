package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.mapper.ChildMapper;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.interest.repository.InterestRepository;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChildServiceImplTest {

    @Mock
    private ChildRepository childRepository;
    @Mock
    private FamilyRepository familyRepository;
    @Mock
    private InterestRepository interestRepository;
    @Mock
    private ChildMapper childMapper;

    @InjectMocks
    private ChildServiceImpl childService;

    private FamilyEntity family;
    private UserEntity user;
    private ChildEntity child;
    private ChildRequestDTO requestDTO;
    private ChildResponseDTO responseDTO;
    private final String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        user.setEmail(userEmail);

        family = new FamilyEntity();
        family.setId(1L);
        family.setUser(user);

        child = new ChildEntity();
        child.setId(10L);
        child.setGender(Gender.BOY);
        child.setBirthDate(LocalDate.of(2015, 1, 1));
        child.setFamily(family);
        child.setInterests(new HashSet<>());

        requestDTO = new ChildRequestDTO(LocalDate.of(2015, 1, 1), Gender.BOY, new HashSet<>(Arrays.asList(2L, 3L, 4L)));
        responseDTO = new ChildResponseDTO(10L, Gender.BOY, 1L);
    }

    @Test
    void create_ShouldReturnChildResponseDTO() {
        when(familyRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(family));
        when(interestRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(childRepository.save(any())).thenReturn(child);
        when(childMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ChildResponseDTO result = childService.create(requestDTO, userEmail);

        assertThat(result.id()).isEqualTo(10L);
        verify(childRepository).save(any());
    }

    @Test
    void update_ShouldReturnUpdatedChildResponseDTO() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(interestRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(childRepository.save(any())).thenReturn(child);
        when(childMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ChildResponseDTO result = childService.update(10L, requestDTO, userEmail);

        assertThat(result.id()).isEqualTo(10L);
        verify(childRepository).save(any());
    }

    @Test
    void delete_ShouldRemoveChild() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));

        childService.delete(10L, userEmail);

        verify(childRepository).delete(child);
    }

    @Test
    void delete_ShouldThrowWhenNotFound() {
        when(childRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> childService.delete(99L, userEmail));
    }

    @Test
    void getById_ShouldReturnResponseDTO() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(childMapper.toResponseDTO(child)).thenReturn(responseDTO);

        ChildResponseDTO result = childService.getById(10L, userEmail);

        assertThat(result.id()).isEqualTo(10L);
        verify(childRepository).findById(10L);
    }

    @Test
    void getById_ShouldThrowWhenNotFound() {
        when(childRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> childService.getById(99L, userEmail));
    }

    @Test
    void getAllSummaries_ShouldReturnMappedList() {
        ChildSummaryDTO summary = new ChildSummaryDTO(10L, Gender.BOY, 8);
        when(childRepository.findAll()).thenReturn(Collections.singletonList(child));
        when(childMapper.toSummaryDTO(child)).thenReturn(summary);

        List<ChildSummaryDTO> result = childService.getAllSummaries();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
    }

    @Test
    void update_ShouldThrowSecurityExceptionWhenNotOwner() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        // Simular otro usuario distinto
        user.setEmail("other@example.com");

        assertThrows(SecurityException.class, () -> childService.update(10L, requestDTO, userEmail));
    }
}


