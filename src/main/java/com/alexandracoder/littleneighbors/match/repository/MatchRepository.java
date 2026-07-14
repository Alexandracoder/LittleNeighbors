package com.alexandracoder.littleneighbors.match.repository;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long>, JpaSpecificationExecutor<MatchEntity> {

    @EntityGraph(attributePaths = {
            "childRequest.family.user",
            "childTarget.family.user"
    })
    List<MatchEntity> findByChildRequestFamilyUserEmail(String email);

    @EntityGraph(attributePaths = {
            "childRequest.family.user",
            "childTarget.family.user"
    })
    List<MatchEntity> findByChildTargetFamilyUserEmail(String email);

    // Variante de findById que trae ya cargadas las familias/usuarios de
    // ambos niños. Se usa fuera de un contexto @Transactional normal (p.ej.
    // en la verificación de suscripciones WebSocket), donde un simple
    // findById() + acceso a childRequest.getFamily() (LAZY) dispararía un
    // LazyInitializationException al no haber sesión de Hibernate abierta.
    @EntityGraph(attributePaths = {
            "childRequest.family.user",
            "childTarget.family.user"
    })
    java.util.Optional<MatchEntity> findWithFamiliesById(Long id);
}