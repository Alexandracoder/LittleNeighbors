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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChildServiceImplTest {

    @Mock private ChildRepository childRepository;
    @Mock private FamilyRepository familyRepository;
    @Mock private InterestRepository interestRepository;
    @Mock private ChildMapper childMapper;

    @InjectMocks private ChildServiceImpl childService;

    private ChildRequestDTO requestDTO;
    private ChildEntity childEntity;
    private FamilyEntity familyEntity;
    private ChildResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simulamos que el niño nació hace 10 años
        LocalDate birthDate = LocalDate.now().minusYears(10);

        requestDTO = new ChildRequestDTO(
                birthDate,
                Gender.FEMALE,
                1L,
                Set.of(2L, 3L)
        );

        familyEntity = new FamilyEntity();
        familyEntity.setId(1L);

        childEntity = new ChildEntity();
        childEntity.setId(5L);
        childEntity.setBirthDate(birthDate);
        childEntity.setGender(Gender.FEMALE);
        childEntity.setFamily(familyEntity);

        responseDTO = new ChildResponseDTO(
                5L,
                birthDate,
                Gender.FEMALE,
                1L,
                Set.of("music", "dance")
        );
    }

    @Nested
    class CreateChild {

        @Test
        void shouldCreateChildSuccessfully() {
            when(childMapper.toEntity(requestDTO)).thenReturn(childEntity);
            when(familyRepository.findById(1L)).thenReturn(Optional.of(familyEntity));
            when(interestRepository.findAllById(Set.of(2L, 3L)))
                    .thenReturn(List.of(new InterestEntity(), new InterestEntity()));
            when(childRepository.save(childEntity)).thenReturn(childEntity);
            when(childMapper.toResponseDTO(childEntity)).thenReturn(responseDTO);

            ChildResponseDTO result = childService.create(requestDTO);

            assertNotNull(result);
            assertEquals(Gender.FEMALE, result.gender());
            verify(childRepository).save(childEntity);
        }

        @Test
        void shouldThrowWhenFamilyNotFound() {
            when(familyRepository.findById(1L)).thenReturn(Optional.empty());
            when(childMapper.toEntity(requestDTO)).thenReturn(childEntity);

            assertThrows(EntityNotFoundException.class, () -> childService.create(requestDTO));
        }
    }

    @Nested
    class UpdateChild {

        @Test
        void shouldUpdateExistingChild() {
            when(childRepository.findById(5L)).thenReturn(Optional.of(childEntity));
            when(familyRepository.findById(1L)).thenReturn(Optional.of(familyEntity));
            when(childRepository.save(childEntity)).thenReturn(childEntity);
            when(childMapper.toResponseDTO(childEntity)).thenReturn(responseDTO);

            ChildResponseDTO result = childService.update(5L, requestDTO);

            assertEquals(Gender.FEMALE, result.gender());
            verify(childRepository).save(childEntity);
        }

        @Test
        void shouldThrowWhenChildNotFoundOnUpdate() {
            when(childRepository.findById(5L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> childService.update(5L, requestDTO));
        }
    }

    @Nested
    class DeleteChild {

        @Test
        void shouldDeleteChildSuccessfully() {
            when(childRepository.findById(5L)).thenReturn(Optional.of(childEntity));

            childService.delete(5L);

            verify(childRepository).delete(childEntity);
        }

        @Test
        void shouldThrowWhenChildNotFoundOnDelete() {
            when(childRepository.findById(5L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> childService.delete(5L));
        }
    }

    @Nested
    class GetChildById {

        @Test
        void shouldReturnChildById() {
            when(childRepository.findById(5L)).thenReturn(Optional.of(childEntity));
            when(childMapper.toResponseDTO(childEntity)).thenReturn(responseDTO);

            ChildResponseDTO result = childService.getById(5L);

            assertEquals(5L, result.id());
        }

        @Test
        void shouldThrowWhenChildNotFound() {
            when(childRepository.findById(5L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> childService.getById(5L));
        }
    }

    @Nested
    class GetAllSummaries {

        @Test
        void shouldReturnAllChildSummaries() {
            ChildSummaryDTO summary = new ChildSummaryDTO(5L, LocalDate.now().minusYears(10), Gender.FEMALE);
            when(childRepository.findAll()).thenReturn(List.of(childEntity));
            when(childMapper.toSummaryDTO(childEntity)).thenReturn(summary);

            List<ChildSummaryDTO> result = childService.getAllSummaries();

            assertEquals(1, result.size());
            assertEquals(Gender.FEMALE, result.get(0).gender());
        }
    }
}
