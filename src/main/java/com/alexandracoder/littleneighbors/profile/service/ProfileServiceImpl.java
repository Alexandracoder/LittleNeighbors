package com.alexandracoder.littleneighbors.profile.service;

import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.mapper.UserMapper;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(String email) {
        log.info("Buscando perfil para: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        UserProfileDTO dto = userMapper.toProfileDTO(user);

        log.info("DTO generado: {}", dto);

        return dto;
    }
}