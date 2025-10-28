package io.kovin.dispatch.management.system.facade;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.mapper.TrailerMapper;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TrailerEntity;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.TrailerData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.CriteriaService;
import io.kovin.dispatch.management.system.service.TrailerService;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import io.kovin.dispatch.management.system.validation.TrailerValidationService;
import lombok.RequiredArgsConstructor;
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
    public TrailerData createTrailer(CreateTrailerRequest request) {
        if (request == null) {
            return TrailerData.builder().build();
        }

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        trailerValidationService.validateTrailerCreation(request);
        TrailerEntity trailerEntity = trailerMapper.fromCreateTrailerRequestToTrailerEntity(request, company);
        trailerService.saveTrailerEntity(trailerEntity);
        return trailerMapper.fromTrailerEntityToTrailerData(trailerEntity);
    }

    public List<TrailerData> getTrailersByCriteria(Map<String, String> queryParams, int page, int size) {
        List<SearchCriteria> searchCriteria = SearchCriteriaUtils.getSearchCriteriaListFromQueryParams(queryParams);
        List<TrailerEntity> trucks = criteriaService.getCollection(searchCriteria, TrailerEntity.class, page, size);
        return trucks.stream().map(trailerMapper::fromTrailerEntityToTrailerData).toList();
    }
}
