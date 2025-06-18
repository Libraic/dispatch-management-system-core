package io.kovin.dispatch.management.system.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.UserCompanyMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserCompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.repository.UserCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCompanyService {

    private final UserCompanyRepository userCompanyRepository;
    private final UserCompanyMapper userCompanyMapper;

    public List<UserCompanyEntity> saveUserCompanies(
        UserEntity user,
        List<CompanyEntity> companies,
        Map<String, BigDecimal> workloadCommission
    ) {
        List<UserCompanyEntity> userCompanyEntities = userCompanyMapper
            .fromUserEntityAndCompanyEntitiesToUserCompanyEntities(
                user,
                companies,
                workloadCommission
            );
        return userCompanyRepository.saveAll(userCompanyEntities);
    }
}
