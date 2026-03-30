package com.alexandracoder.littleneighbors.neighborhood.service;

import com.alexandracoder.littleneighbors.city.entity.CityEntity;
import com.alexandracoder.littleneighbors.city.repository.CityRepository;
import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodRequestDTO;
import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodResponseDTO;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.neighborhood.mapper.NeighborhoodMapper;
import com.alexandracoder.littleneighbors.neighborhood.repository.NeighborhoodRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NeighborhoodServiceImpl implements NeighborhoodService {

    private final NeighborhoodRepository neighborhoodRepository;
    private final CityRepository cityRepository;
    private final NeighborhoodMapper neighborhoodMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NeighborhoodResponseDTO> getAll(Pageable pageable) {
        return neighborhoodRepository.findAll(pageable)
                .map(neighborhoodMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public NeighborhoodResponseDTO getById(Long id) {
        return neighborhoodMapper.toResponseDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public NeighborhoodResponseDTO create(NeighborhoodRequestDTO dto) {
        CityEntity city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + dto.cityId()));

        NeighborhoodEntity entity = new NeighborhoodEntity();
        entity.setName(dto.name());
        entity.setStreetName(dto.streetName());
        entity.setPostalCode(dto.postalCode());
        entity.setCity(city);

        return neighborhoodMapper.toResponseDTO(neighborhoodRepository.save(entity));
    }

    @Override
    @Transactional
    public NeighborhoodResponseDTO update(Long id, NeighborhoodRequestDTO dto) {
        NeighborhoodEntity entity = findOrThrow(id);

        CityEntity city = cityRepository.findById(dto.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + dto.cityId()));

        entity.setName(dto.name());
        entity.setStreetName(dto.streetName());
        entity.setPostalCode(dto.postalCode());
        entity.setCity(city);

        return neighborhoodMapper.toResponseDTO(neighborhoodRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        NeighborhoodEntity entity = findOrThrow(id);

        if (!entity.getFamilies().isEmpty()) {
            throw new BusinessLogicException("Cannot delete neighborhood with associated families");
        }

        neighborhoodRepository.delete(entity);
    }

    private NeighborhoodEntity findOrThrow(Long id) {
        return neighborhoodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Neighborhood not found with id: " + id));
    }
}