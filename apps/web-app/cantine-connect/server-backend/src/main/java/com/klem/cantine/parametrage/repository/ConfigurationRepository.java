package com.klem.cantine.parametrage.repository;

import com.klem.cantine.parametrage.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    Optional<Configuration> findByCle(String cle);
}
