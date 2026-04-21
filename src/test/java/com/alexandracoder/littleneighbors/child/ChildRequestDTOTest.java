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

/**
 * Unit tests for the compact constructor of {@link ChildRequestDTO}.
 *
 * <p>These tests exercise the business rules enforced inline by the Record constructor,
 * independently of Spring's {@code @Valid} annotation processing.
 *
 * <p>All assertions and display names are in English as required.
 */
@DisplayName("ChildRequestDTO — compact constructor validation")
class ChildRequestDTOTest {

    // ─────────────────────────────────────────────────────────────────────────
    // BORN life stage — mandatory fields
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("When lifeStage is BORN")
    class BornLifeStage {

        @Test
        @DisplayName("should throw IllegalArgumentException when birthDate is null")
        void constructor_bornWithNullBirthDate_throwsWithCorrectMessage() {

            IllegalArgumentException ex = catchThrowableOfType(
                    () -> new ChildRequestDTO(
                            null,               // birthDate — missing!
                            null,
                            LifeStage.BORN,
                            Gender.BOY,
                            Set.of(),
                            false,
                            "Loves painting"
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
                            LocalDate.of(2021, 3, 10),
                            null,
                            LifeStage.BORN,
                            null,               // gender — missing!
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
                            LocalDate.of(2021, 3, 10),
                            null,
                            LifeStage.BORN,
                            Gender.GIRL,
                            Set.of(1L, 2L),
                            false,
                            "A cheerful and creative child."
                    )
            );
        }

        @Test
        @DisplayName("should construct successfully when description is null (optional)")
        void constructor_bornWithNullDescription_constructsSuccessfully() {

            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            LocalDate.of(2020, 11, 5),
                            null,
                            LifeStage.BORN,
                            Gender.BOY,
                            Set.of(),
                            false,
                            null   // description is optional
                    )
            );
        }

        @Test
        @DisplayName("should construct successfully when description is exactly 500 characters")
        void constructor_bornWithDescriptionAt500Chars_constructsSuccessfully() {

            String exactly500 = "X".repeat(500);

            // The constructor does NOT enforce the 500-char limit — that is @Size on the field,
            // validated by Spring's @Valid at the HTTP layer. Constructor should not throw here.
            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            LocalDate.of(2020, 11, 5),
                            null,
                            LifeStage.BORN,
                            Gender.BOY,
                            Set.of(),
                            false,
                            exactly500
                    )
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PREGNANCY life stage — optional fields
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("When lifeStage is PREGNANCY")
    class PregnancyLifeStage {

        @Test
        @DisplayName("should construct successfully even when birthDate and gender are null")
        void constructor_pregnancyWithNullBirthDateAndGender_constructsSuccessfully() {

            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            null,           // birthDate — allowed for PREGNANCY
                            LocalDate.now().plusMonths(3),
                            LifeStage.PREGNANCY,
                            null,           // gender — allowed for PREGNANCY
                            Set.of(),
                            true,
                            "Expecting soon!"
                    )
            );
        }

        @Test
        @DisplayName("should construct successfully with a valid description")
        void constructor_pregnancyWithDescription_constructsSuccessfully() {

            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            null,
                            LocalDate.now().plusMonths(2),
                            LifeStage.PREGNANCY,
                            null,
                            Set.of(),
                            true,
                            "Looking forward to meeting our little one!"
                    )
            );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Default values enforced by the compact constructor
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Default value normalisation")
    class DefaultValues {

        @Test
        @DisplayName("should default interestIds to an empty Set when null is passed")
        void constructor_withNullInterestIds_defaultsToEmptySet() {

            ChildRequestDTO dto = new ChildRequestDTO(
                    LocalDate.of(2019, 7, 4),
                    null,
                    LifeStage.BORN,
                    Gender.BOY,
                    null,   // interestIds — should be normalised to Set.of()
                    false,
                    "Active child"
            );

            assertThat(dto.interestIds()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should default isPrenatal to false when null is passed")
        void constructor_withNullIsPrenatal_defaultsToFalse() {

            ChildRequestDTO dto = new ChildRequestDTO(
                    LocalDate.of(2019, 7, 4),
                    null,
                    LifeStage.BORN,
                    Gender.GIRL,
                    Set.of(),
                    null,   // isPrenatal — should be normalised to false
                    "Loves music"
            );

            assertThat(dto.isPrenatal()).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Parameterised — edge cases for description
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Description field — various inputs")
    class DescriptionEdgeCases {

        @ParameterizedTest(name = "description = \"{0}\"")
        @ValueSource(strings = {
                "",
                " ",
                "Hello",
                "A short bio.",
                "Emoji friendly 🎨🚀"
        })
        @DisplayName("should construct successfully for various non-null description values")
        void constructor_withVariousDescriptions_constructsSuccessfully(String description) {

            assertThatNoException().isThrownBy(() ->
                    new ChildRequestDTO(
                            LocalDate.of(2022, 1, 1),
                            null,
                            LifeStage.BORN,
                            Gender.BOY,
                            Set.of(),
                            false,
                            description
                    )
            );
        }
    }
}
