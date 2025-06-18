package io.kovin.dispatch.management.system.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.kovin.dispatch.management.system.mapper.UserMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.NoteEntity;
import io.kovin.dispatch.management.system.model.entity.UserCompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.request.CreateUserRequest;
import io.kovin.dispatch.management.system.model.request.CreateWorkloadRequest;
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
    private final NoteService noteService;

    public UserEntity createUser(CreateUserRequest request) {
        userValidation.validateUserCreation(request);
        UserEntity userEntity = userMapper.fromCreateUserRequestToUserEntity(request);
        UserEntity createdUserEntity = userRepository.save(userEntity);
        List<CreateWorkloadRequest> workloads = Optional.ofNullable(request.workloads()).orElse(List.of());
        List<String> companiesUuids = new ArrayList<>();
        Map<String, BigDecimal> workloadCommission = new HashMap<>();
        for (CreateWorkloadRequest workload : workloads) {
            String companyUuid = workload.companyUuid();
            companiesUuids.add(companyUuid);
            workloadCommission.put(companyUuid, BigDecimal.valueOf(workload.commission()));
        }

        List<CompanyEntity> companies = companyService.findByUuids(companiesUuids);
        List<UserCompanyEntity> userCompanies = userCompanyService.saveUserCompanies(userEntity, companies, workloadCommission);
        log.info("The user will be responsible for [{}] companies.", userCompanies.size());
        List<NoteEntity> noteEntities = noteService.saveNotes(request.notes(), createdUserEntity);
        log.info("The user has [{}] notes created.", noteEntities.size());
        return createdUserEntity;
    }
}
