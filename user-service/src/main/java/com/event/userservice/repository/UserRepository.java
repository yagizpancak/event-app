package com.event.userservice.repository;

import com.event.userservice.model.ApplicationUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUsers , Integer> {
    @Transactional
    Optional<ApplicationUsers> findByUsername(String username);
}
