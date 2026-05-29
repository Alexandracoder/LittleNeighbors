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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("smoke")
@DisplayName("FamilyRepository — persistence smoke tests")
@Sql(statements = {
        "SET FOREIGN_KEY_CHECKS = 0;",
        "TRUNCATE TABLE families;",
        "TRUNCATE TABLE neighborhoods;",
        "TRUNCATE TABLE cities;",
        "TRUNCATE TABLE users;",
        "SET FOREIGN_KEY_CHECKS = 1;"
})
class PersistenceSmokeTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    FamilyRepository familyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NeighborhoodRepository neighborhoodRepository;
    @Autowired
    CityRepository cityRepository;

    private CityEntity city;
    private NeighborhoodEntity neighbourhood;
    private UserEntity user;

    @BeforeEach
    void seedFixtures() {

        city = CityEntity.builder()
                .name("Valencia")
                .build();
        em.persist(city);

        neighbourhood = NeighborhoodEntity.builder()
                .name("Benimaclet")
                .streetName("Carrer del Barón de San Petrillo")
                .postalCode("46020")
                .city(city)
                .build();
        em.persist(neighbourhood);

        user = UserEntity.builder()
                .email("lucia@smoketest.com")
                .firstName("Lucía")
                .lastName("García")
                .password("$2a$10$hashed")
                .roles(Set.of(Role.FAMILY))
                .verificationStatus(VerificationStatus.VERIFIED)
                .build();
        em.persist(user);

        em.flush();
    }

    @Nested
    @DisplayName("Basic CRUD")
    class BasicCrud {

        @Test
        @DisplayName("should persist a Family and read it back with all fields intact")
        void persistFamily_allFieldsRoundTrip() {
            FamilyEntity family = buildFamily("Familia García", user, neighbourhood);
            FamilyEntity saved  = familyRepository.save(family);
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