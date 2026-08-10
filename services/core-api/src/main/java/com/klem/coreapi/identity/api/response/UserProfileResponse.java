package com.klem.coreapi.identity.api.response;

import com.klem.coreapi.identity.domain.model.TenantMembership;
import com.klem.coreapi.identity.domain.model.User;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String displayName,
        List<TenantMembershipResponse> tenants
) {

    public static UserProfileResponse from(User user, List<TenantMembership> memberships) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                memberships.stream().map(TenantMembershipResponse::from).toList()
        );
    }
}
