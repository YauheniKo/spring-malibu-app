package com.malibu.app.repository;


import com.malibu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  Optional<User> getUserByEmail(String email);

  Boolean existsByUsername(String username);

  User findByEmail(String email);

  Boolean existsByEmail(String email);
}