package com.barbup.barbup_api.services;

import lombok.RequiredArgsConstructor;
import com.barbup.barbup_api.domain.entity.user.User;
import com.barbup.barbup_api.shared.dto.auth.UpdateRequestDTO;
import com.barbup.barbup_api.infra.persistence.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User updateUser(UpdateRequestDTO dto, User userAuthenticated) {
        var user = userRepository.getReferenceById(userAuthenticated.getId());

        user.setName(dto.name());
        user.setPhone(dto.phone());

        return this.userRepository.save(user);
    }
}
