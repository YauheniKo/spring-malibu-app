package com.malibu.springmalibuapp.repository;

import com.malibu.springmalibuapp.model.ERole;
import com.malibu.springmalibuapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
