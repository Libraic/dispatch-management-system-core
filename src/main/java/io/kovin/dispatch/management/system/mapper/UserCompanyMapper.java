package io.kovin.dispatch.management.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserCompanyEntity;
import io.kovin.dispatch.management.system.model.entity.UserCompanyId;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserCompanyMapper {

    public List<UserCompanyEntity> fromUserEntityAndCompanyEntitiesToUserCompanyEntities(
        UserEntity user,
        List<CompanyEntity> companies,
        Map<String, BigDecimal> workloadCommission
    ) {
        return companies.stream()
            .map(company -> UserCompanyEntity.builder()
                .userCompanyId(UserCompanyId.builder().userId(user.getId()).companyId(company.getId()).build())
                .user(user)
                .company(company)
                .commission(workloadCommission.get(company.getUuid()))
                .build()
            ).toList();
    }
}
