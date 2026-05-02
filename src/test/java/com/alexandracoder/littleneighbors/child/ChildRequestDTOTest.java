package com.alexandracoder.littleneighbors.child;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ChildRequestDTO — compact constructor validation")
class ChildRequestDTOTest {

    // ORDEN DEL RECORD: nickname, birthDate, dueDate, lifeStage, gender, interestIds, isPrenatal, description

    @Nested
    @DisplayName("When lifeStage is BORN")
    class BornLifeStage {

        @Test
        @DisplayName("should throw IllegalArgumentException when birthDate is null")
        void constructor_bornWithNullBirthDate_throwsWithCorrectMessage() {
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> new ChildRequestDTO(
                            "Leo",              // 1. nickname
                            null,               // 2. birthDate — ¡Faltante!
                            null,               // 3. dueDate
                            LifeStage.BORN,     // 4. lifeStage
                            Gender.BOY,         // 5. gender
                            Set.of(),           // 6. interestIds
                            false,              // 7. isPrenatal
                            "Loves painting"    // 8. description
                    ),
                    IllegalArgumentException.class
            );

            assertThat(ex)
                    .isNotNull()
                    .hasMessage("Birth date is required for born children");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when gender is null")
        void constructor_bornWithNullGender_throwsWithCorrectMessage() {
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> new ChildRequestDTO(
                            "Leo",
                            LocalDate.of(2021, 3, 10),
                            null,
                            LifeStage.BORN,
                            null,               // 5. gender — ¡Faltante!
                            Set.of(),
                            false,
                            "Loves football"
                    ),
                    IllegalArgumentException.class
            );

            assertThat(ex)
                    .isNotNull()
                    .hasMessage("Gender is required for born children");
        }

        @Test
        @DisplayName("should construct successfully when all required fields are present")
        void constructor_bornWithAllRequiredFields_constructsSuccessfully() {
            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            "Leo",
                            LocalDate.of(2021, 3, 10),
                            null,
                            LifeStage.BORN,
                            Gender.GIRL,
                            Set.of(1L, 2L),
                            false,
                            "A cheerful child."
                    )
            );
        }
    }

    @Nested
    @DisplayName("When lifeStage is PREGNANCY")
    class PregnancyLifeStage {

        @Test
        @DisplayName("should construct successfully even when birthDate and gender are null")
        void constructor_pregnancyWithNullBirthDateAndGender_constructsSuccessfully() {
            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            "Future Baby",
                            null,           // birthDate permitido null en PREGNANCY
                            LocalDate.now().plusMonths(3),
                            LifeStage.PREGNANCY,
                            null,           // gender permitido null en PREGNANCY
                            Set.of(),
                            true,
                            "Expecting soon!"
                    )
            );
        }
    }

    @Nested
    @DisplayName("Default value normalisation")
    class DefaultValues {

        @Test
        @DisplayName("should default interestIds to an empty Set when null is passed")
        void constructor_withNullInterestIds_defaultsToEmptySet() {
            ChildRequestDTO dto = new ChildRequestDTO(
                    "Leo",
                    LocalDate.of(2019, 7, 4),
                    null,
                    LifeStage.BORN,
                    Gender.BOY,
                    null,   // 6. interestIds -> se normaliza a Set.of()
                    false,
                    "Active child"
            );

            assertThat(dto.interestIds()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should default isPrenatal to false when null is passed")
        void constructor_withNullIsPrenatal_defaultsToFalse() {
            ChildRequestDTO dto = new ChildRequestDTO(
                    "Leo",
                    LocalDate.of(2019, 7, 4),
                    null,
                    LifeStage.BORN,
                    Gender.GIRL,
                    Set.of(),
                    null,   // 7. isPrenatal -> se normaliza a false
                    "Loves music"
            );

            assertThat(dto.isPrenatal()).isFalse();
        }
    }

    @Nested
    @DisplayName("Description field — various inputs")
    class DescriptionEdgeCases {

        @ParameterizedTest(name = "description = \"{0}\"")
        @ValueSource(strings = {"", " ", "Hello", "Emoji friendly 🎨"})
        @DisplayName("should construct successfully for various description values")
        void constructor_withVariousDescriptions_constructsSuccessfully(String desc) {
            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            "Leo",
                            LocalDate.of(2022, 1, 1),
                            null,
                            LifeStage.BORN,
                            Gender.BOY,
                            Set.of(),
                            false,
                            desc
                    )
            );
        }
    }
}
