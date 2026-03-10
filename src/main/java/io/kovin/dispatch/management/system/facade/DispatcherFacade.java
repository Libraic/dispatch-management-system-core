package io.kovin.dispatch.management.system.facade;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.DispatcherEntity;
import io.kovin.dispatch.management.system.model.request.CreateDispatcherRequest;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.DispatcherService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.DispatcherValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DispatcherFacade {

    private final CompanyService companyService;
    private final CriteriaService criteriaService;
    private final DispatcherValidationService dispatcherValidationService;
    private final DispatcherService dispatcherService;

    @Transactional
    public void createDispatcher(CreateDispatcherRequest request) {
        if (request == null) {
            return;
        }

        dispatcherValidationService.validateDispatcherCreation(request);

        CompanyEntity company = companyService.getByUuid(request.companyUuid());

        DispatcherEntity dispatcherEntity = DispatcherEntity.builder()
            .uuid(UUID.randomUUID().toString())
            .name(request.name())
            .phoneNumber(request.phoneNumber())
            .company(company)
            .build();


        dispatcherService.saveDispatcher(dispatcherEntity);
    }

    public List<GetDispatcherResponse> getDispatchersByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<DispatcherEntity> users = criteriaService.getCollection(searchCriteria, DispatcherEntity.class, page, size);
        return users.stream().map(this::fromDispatcherEntityToGetDispatcherResponse).toList();
    }

    private GetDispatcherResponse fromDispatcherEntityToGetDispatcherResponse(DispatcherEntity dispatcher) {
        return GetDispatcherResponse.builder()
            .uuid(dispatcher.getUuid())
            .name(dispatcher.getName())
            .build();
    }
}
