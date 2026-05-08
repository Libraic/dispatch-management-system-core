package io.kovin.dispatch.management.system.facade;

import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.TrailerMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.persistence.CompanyEntity;
import io.kovin.dispatch.management.system.model.persistence.TrailerEntity;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.GetTrailerResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.TrailerService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.TrailerValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TrailerFacade {

    private final TrailerValidationService trailerValidationService;
    private final TrailerService trailerService;
    private final CompanyService companyService;
    private final CriteriaService criteriaService;

    private final TrailerMapper trailerMapper;

    @Transactional
    public GetTrailerResponse createTrailer(CreateTrailerRequest request) {
        if (request == null) {
            return GetTrailerResponse.builder().build();
        }

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        trailerValidationService.validateTrailerCreation(request);
        TrailerEntity trailerEntity = trailerMapper.fromCreateTrailerRequestToTrailerEntity(request, company);
        trailerService.saveTrailerEntity(trailerEntity);
        return trailerMapper.fromTrailerEntityToTrailerData(trailerEntity);
    }

    public Page<GetTrailerResponse> getTrailersByCriteria(Map<String, String> queryParams, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, DEFAULT_SORTING_FIELD));
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        return criteriaService.getCollection(
            searchCriteria,
            TrailerEntity.class,
            pageable,
            trailerMapper::fromTrailerEntityToTrailerData
        );
    }
}
