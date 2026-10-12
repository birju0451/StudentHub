
package com.studentmanagement.repository;

import com.studentmanagement.entity.InvitationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationTokenRepository
        extends JpaRepository<InvitationToken, Long> {

    Optional<InvitationToken> findByTokenHashAndUsedFalse(
            String tokenHash
    );
}
