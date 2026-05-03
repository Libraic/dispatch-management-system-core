package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.model.persistence.UserEntity;
import io.kovin.dispatch.management.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUserByUsername(String username) {
        log.info("Retrieving user by username=[{}].", username);
        return userRepository.findByUsername(username);
    }
}
