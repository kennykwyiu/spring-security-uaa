package com.kenny.uaa.repository;

import com.kenny.uaa.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
   Optional<User> findOptionalByEmail(String email);
   Optional<User> findOptionalByUsername(String username);
   long countByUsername(String username);
   long countByEmail(String email);
   long countByMobile(String mobile);
}
