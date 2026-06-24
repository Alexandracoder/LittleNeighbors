package com.alexandracoder.littleneighbors.smoke;

import com.alexandracoder.littleneighbors.city.entity.CityEntity;
import com.alexandracoder.littleneighbors.city.repository.CityRepository;
import com.alexandracoder.littleneighbors.enums.FamilyStatus;
import com.alexandracoder.littleneighbors.enums.Role;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Tag("smoke")
@DisplayName("FamilyRepository — persistence smoke tests (Real PostgreSQL)")
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "JWT_SECRET=test_secret_for_integration_tests_123456",
        "ALLOWED_ORIGINS=http://localhost:5173"

})

class PersistenceSmokeTest {
    @MockBean
    private JavaMailSender javaMailSender;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("littleneighbors_smoke_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    EntityManager em;

    @Autowired
    FamilyRepository familyRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    NeighborhoodRepository neighborhoodRepository;

    @Autowired
    UserRepository userRepository;

    private CityEntity city;
    private NeighborhoodEntity neighbourhood;
    private UserEntity user;

    @BeforeEach
    void seedFixtures() {

        city = cityRepository.findByNameIgnoreCase("Valencia")
                .orElseGet(() -> {
                    CityEntity newCity = CityEntity.builder().name("Valencia").build();
                    return cityRepository.save(newCity);
                });


        neighbourhood = neighborhoodRepository.findByNameIgnoreCase("Benimaclet")
                .orElseGet(() -> {
                    NeighborhoodEntity nb = NeighborhoodEntity.builder()
                            .name("Benimaclet")
                            .streetName("Carrer del Barón de San Petrillo")
                            .postalCode("46020")
                            .city(city)
                            .build();
                    return neighborhoodRepository.save(nb);
                });


        user = userRepository.findByEmail("lucia@smoketest.com")
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .email("lucia@smoketest.com")
                            .firstName("Lucía")
                            .lastName("García")
                            .password("$2a$10$hashed")
                            .roles(Set.of(Role.FAMILY))
                            .verificationStatus(VerificationStatus.VERIFIED)
                            .build();
                    return userRepository.save(newUser);
                });

        em.flush();
    }

    @Nested
    @DisplayName("Basic CRUD")
    class BasicCrud {

        @Test
        @DisplayName("should persist a Family and read it back with all fields intact")
        void persistFamily_allFieldsRoundTrip() {
            FamilyEntity family = buildFamily("Familia García", user, neighbourhood);
            FamilyEntity saved = familyRepository.save(family);
            em.flush();
            em.clear();

            Optional<FamilyEntity> found = familyRepository.findById(saved.getId());
            assertThat(found).isPresent();
            FamilyEntity loaded = found.get();

            assertThat(loaded.getFamilyName()).isEqualTo("Familia García");
            assertThat(loaded.getRepresentativeName()).isEqualTo("Lucía García");
            assertThat(loaded.getStatus()).isEqualTo(FamilyStatus.ESTABLISHED_FAMILY);
            assertThat(loaded.getUser().getEmail()).isEqualTo("lucia@smoketest.com");
            assertThat(loaded.getNeighborhood().getName()).isEqualTo("Benimaclet");
        }
    }

    private FamilyEntity buildFamily(String familyName, UserEntity owner, NeighborhoodEntity nb) {
        return FamilyEntity.builder()
                .familyName(familyName)
                .representativeName(owner.getFirstName() + " " + owner.getLastName())
                .description("Somos una familia acogedora.")
                .status(FamilyStatus.ESTABLISHED_FAMILY)
                .user(owner)
                .neighborhood(nb)
                .build();
    }
}