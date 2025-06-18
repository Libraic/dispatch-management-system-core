package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.mapper.UserMapper;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.UserData;
import io.kovin.dispatch.management.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<UserData>> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("A request to create a User was received.");
        UserEntity userEntity = userService.createUser(createUserRequest);
        UserData userData = userMapper.fromUserEntityToUserData(userEntity);
        return ResponseEntity.ok(ApiResponse.fromData(userData));
    }
}
