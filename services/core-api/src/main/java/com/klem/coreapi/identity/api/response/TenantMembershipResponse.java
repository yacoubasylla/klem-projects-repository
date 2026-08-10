package com.klem.coreapi.identity.api.response;

import com.klem.coreapi.identity.domain.model.MembershipStatus;
import com.klem.coreapi.identity.domain.model.TenantMembership;

import java.time.Instant;
import java.util.UUID;

public record TenantMembershipResponse(
        UUID membershipId,
        UUID tenantId,
        MembershipStatus status,
        Instant invitedAt,
        Instant activatedAt
) {

    public static TenantMembershipResponse from(TenantMembership membership) {
        return new TenantMembershipResponse(
                membership.getId(),
                membership.getTenantId(),
                membership.getStatus(),
                membership.getInvitedAt(),
                membership.getActivatedAt()
        );
    }
}
