package com.klem.coreapi.authorization.domain.exception;

import com.klem.coreapi.shared.domain.BadRequestException;

import java.util.UUID;

/**
 * Un rôle ne peut être attribué qu'à un utilisateur déjà rattaché au tenant concerné
 * ({@code identity.TenantMembership}) — sinon l'attribution n'a pas de sens (à quel titre agirait-il
 * dans ce tenant ?).
 */
public class UserNotMemberOfTenantException extends BadRequestException {

    public UserNotMemberOfTenantException(UUID userId, UUID tenantId) {
        super("L'utilisateur " + userId + " n'est pas membre du tenant " + tenantId);
    }
}
