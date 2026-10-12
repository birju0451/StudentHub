
package com.studentmanagement.repository;
import com.studentmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //   Find user by email
    //   Spring Data JPA understands the method name and generates the query.
    Optional<User> findByEmail(String email);

    //   Check whether email exists
    //   We'll use this during registration to prevent duplicate accounts
    boolean existsByEmail(String email);
}

