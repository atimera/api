package com.atimera.api.repositories;

import com.atimera.api.domain.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Server findByIpAddress(String ipAddress);
}
