package com.dgomesdev.to_do_list_api.domain.repository;

import com.dgomesdev.to_do_list_api.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsername(String username);

    Boolean existsByUsername(String username);
}