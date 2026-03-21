package com.alexandracoder.littleneighbors.user.dto;

public record UserStatusDTO(
        boolean hasFamily,
        boolean hasChildren,
        boolean isRegistrationComplete
) {

    public UserStatusDTO(boolean hasFamily, boolean hasChildren) {
        this(hasFamily, hasChildren, hasFamily && hasChildren);
    }
}