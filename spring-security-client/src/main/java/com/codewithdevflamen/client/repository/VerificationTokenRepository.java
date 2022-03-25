package com.codewithdevflamen.client.repository;

import com.codewithdevflamen.client.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
}
