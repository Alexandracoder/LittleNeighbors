package com.alexandracoder.littleneighbors.profile.service;

import com.alexandracoder.littleneighbors.profile.dto.UserProfileDTO;

public interface ProfileService {
    UserProfileDTO getCurrentUserProfile(String email);

}
