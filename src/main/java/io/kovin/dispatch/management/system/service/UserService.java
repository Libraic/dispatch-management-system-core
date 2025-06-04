package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.mapper.UserMapper;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.repository.UserRepository;
import io.kovin.dispatch.management.system.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final UserMapper userMapper;

    public UserEntity createUser(CreateUserRequest request) {
        userValidation.validateUserCreation(request);
        UserEntity userEntity = userMapper.fromCreateUserRequestToUserEntity(request);
        return userRepository.save(userEntity);
    }
}
