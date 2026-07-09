package com.alexandracoder.littleneighbors.user.mapper;

import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final FamilyMapper familyMapper;

    public UserProfileDTO toProfileDTO(UserEntity entity) {
        if (entity == null) return null;

        List<String> roles = entity.getRoles().stream()
                .map(Enum::name)
                .toList();

        FamilyResponseDTO familyDto = (entity.getFamily() != null)
                ? familyMapper.toResponse(entity.getFamily())
                : null;

        return new UserProfileDTO(
                entity.getEmail(),
                roles,
                familyDto,
                entity.getVerificationStatus()
        );
    }
}