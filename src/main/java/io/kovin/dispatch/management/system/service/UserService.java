package io.kovin.dispatch.management.system.service;

import java.util.List;
import io.kovin.dispatch.management.system.mapper.UserMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserCompanyEntity;
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
    private final CompanyService companyService;
    private final UserCompanyService userCompanyService;

    public UserEntity createUser(CreateUserRequest request) {
        userValidation.validateUserCreation(request);
        UserEntity userEntity = userMapper.fromCreateUserRequestToUserEntity(request);
        UserEntity createdUserEntity = userRepository.save(userEntity);
        List<CompanyEntity> companies = companyService.findByUuids(request.companiesUuids());
        List<UserCompanyEntity> userCompanies = userCompanyService.saveUserCompanies(userEntity, companies);
        log.info("The user will be responsible for [{}] companies.", userCompanies.size());
        return createdUserEntity;
    }
}
