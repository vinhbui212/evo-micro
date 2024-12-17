package org.example.thuan_security.repository;

import org.example.thuan_security.model.ServiceClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceClientRepo extends JpaRepository<ServiceClient, Long>
{
    ServiceClient findByClientIdAndClientSecret(String clientId, String clientSecret);
}
