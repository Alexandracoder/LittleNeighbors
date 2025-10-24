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
import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import com.alexandracoder.littleneighbors.interest.repository.InterestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private ChildEntity child;
    private ChildRequestDTO requestDTO;
    private ChildResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        family = new FamilyEntity();
        family.setId(1L);

        child = new ChildEntity();
        child.setId(10L);
        child.setGender(Gender.MALE);
        child.setFamily(family);
        child.setInterests(new HashSet<>());

        requestDTO = new ChildRequestDTO(Gender.MALE, 1L, new HashSet<>(Arrays.asList(2L, 3L)));
        responseDTO = new ChildResponseDTO(10L, Gender.MALE, 1L);
    }

    @Test
    void create_ShouldReturnChildResponseDTO() {
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(interestRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(childMapper.toEntity(requestDTO)).thenReturn(child);
        when(childRepository.save(any())).thenReturn(child);
        when(childMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ChildResponseDTO result = childService.create(requestDTO);

        assertThat(result.id()).isEqualTo(10L);
        verify(childRepository).save(any());
    }

    @Test
    void update_ShouldReturnUpdatedChildResponseDTO() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(familyRepository.findById(1L)).thenReturn(Optional.of(family));
        when(interestRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(childRepository.save(any())).thenReturn(child);
        when(childMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ChildResponseDTO result = childService.update(10L, requestDTO);

        assertThat(result.id()).isEqualTo(10L);
        verify(childRepository).save(any());
    }

    @Test
    void delete_ShouldRemoveChild() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));

        childService.delete(10L);

        verify(childRepository).delete(child);
    }

    @Test
    void delete_ShouldThrowWhenNotFound() {
        when(childRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> childService.delete(99L));
    }

    @Test
    void getById_ShouldReturnResponseDTO() {
        when(childRepository.findById(10L)).thenReturn(Optional.of(child));
        when(childMapper.toResponseDTO(child)).thenReturn(responseDTO);

        ChildResponseDTO result = childService.getById(10L);

        assertThat(result.id()).isEqualTo(10L);
        verify(childRepository).findById(10L);
    }

    @Test
    void getById_ShouldThrowWhenNotFound() {
        when(childRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> childService.getById(99L));
    }

    @Test
    void getAllSummaries_ShouldReturnMappedList() {
        ChildSummaryDTO summary = new ChildSummaryDTO(10L, Gender.MALE, 1);
        when(childRepository.findAll()).thenReturn(Collections.singletonList(child));
        when(childMapper.toSummaryDTO(child)).thenReturn(summary);

        List<ChildSummaryDTO> result = childService.getAllSummaries();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
    }
}
