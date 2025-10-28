package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.UserMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.UserEntity;
import io.kovin.dispatch.management.system.model.response.UserData;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final CriteriaService criteriaService;

    private final UserMapper userMapper;

    public List<UserData> getUsersByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<UserEntity> users = criteriaService.getCollection(searchCriteria, UserEntity.class, page, size);
        return users.stream().map(userMapper::fromUserEntityToUserData).toList();
    }
}
