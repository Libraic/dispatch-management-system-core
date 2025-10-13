package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kovin.dispatch.management.system.facade.UserFacade;
import io.kovin.dispatch.management.system.mapper.UserMapper;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.model.response.UserData;
import io.kovin.dispatch.management.system.model.response.error.GroupsErrors;
import io.kovin.dispatch.management.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserFacade userFacade;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<UserData, List<GroupsErrors>>> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.info("A request to create a User was received.");
        UserEntity userEntity = userService.createUser(createUserRequest);
        UserData userData = userMapper.fromUserEntityToUserData(userEntity);
        return ResponseEntity.ok(ApiResponse.fromData(userData));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserData>, ErrorResponse>> getUsersByCriteria(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size,
        @RequestParam(name = "firstName", required = false) String firstName,
        @RequestParam(name = "nickname", required = false) String nickname,
        @RequestParam(name = "lastName", required = false) String lastName,
        @RequestParam(name = "fullName", required = false) String fullName
    ) {
        log.info("A request to fetch Users by criteria was received.");
        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", firstName);
        fields.put("nickname", nickname);
        fields.put("lastName", lastName);
        fields.put("fullName", fullName);
        int finalPage = page == null ? 0 : page;
        int finalSize = size == null ? 0 : size;
        List<UserData> usersData = userFacade.getUsersByCriteria(fields, finalPage, finalSize);
        return ResponseEntity.ok(ApiResponse.fromData(usersData));
    }
}
