package com.barbup.barbup_api.services;

import com.barbup.barbup_api.domain.user.User;
import com.barbup.barbup_api.domain.user.dto.UpdateRequestDTO;
import com.barbup.barbup_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public User updateUser(UpdateRequestDTO dto) {
        var user = userRepository.getReferenceById(dto.id());

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());

        return this.userRepository.save(user);
    }
}
