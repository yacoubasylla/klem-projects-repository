package com.klem.coreapi.identity.application.port;

import com.klem.coreapi.identity.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByKeycloakSubject(String keycloakSubject);

    Optional<User> findByEmail(String email);
}
