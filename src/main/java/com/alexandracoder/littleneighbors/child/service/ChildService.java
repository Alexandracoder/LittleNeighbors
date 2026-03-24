package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import java.util.List;

public interface ChildService {

    // ADMIN: Obtener resúmenes de todos los niños
    List<ChildSummaryDTO> getAllSummaries();

    // FAMILY: Listar solo los niños de la familia autenticada
    List<ChildResponseDTO> findAllByFamilyEmail(String email);

    // CRUD de Child: Operaciones estándar
    ChildResponseDTO create(ChildRequestDTO dto, String username);

    ChildResponseDTO update(Long id, ChildRequestDTO dto, String username);

    ChildResponseDTO getById(Long id, String username);

    void deleteByIdAndFamilyEmail(Long id, String email);

    // Nota: Se han eliminado los métodos que devolvían FamilyEntity o FamilyResponseDTO
    // para evitar recursividad infinita en la documentación de Swagger/OpenAPI.
}