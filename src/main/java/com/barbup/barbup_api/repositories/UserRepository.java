package com.barbup.barbup_api.repositories;

import com.barbup.barbup_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByEmail(String email);
}
