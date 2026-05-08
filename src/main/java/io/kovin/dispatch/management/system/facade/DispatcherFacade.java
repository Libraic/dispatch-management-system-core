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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;

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
            .uuid(UUID.randomUUID())
            .name(request.name())
            .phoneNumber(request.phoneNumber())
            .company(company)
            .build();


        dispatcherService.saveDispatcher(dispatcherEntity);
    }

    public Page<GetDispatcherResponse> getDispatchersByCriteria(Map<String, String> queryParams, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, DEFAULT_SORTING_FIELD));
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        return criteriaService.getCollection(
            searchCriteria,
            DispatcherEntity.class,
            pageable,
            this::fromDispatcherEntityToGetDispatcherResponse
        );
    }

    private GetDispatcherResponse fromDispatcherEntityToGetDispatcherResponse(DispatcherEntity dispatcher) {
        return GetDispatcherResponse.builder()
            .uuid(dispatcher.getUuid().toString())
            .name(dispatcher.getName())
            .build();
    }
}
