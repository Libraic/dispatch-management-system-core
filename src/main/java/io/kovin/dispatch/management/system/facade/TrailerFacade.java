package io.kovin.dispatch.management.system.facade;

import io.kovin.dispatch.management.system.mapper.TrailerMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.entity.TrailerEntity;
import io.kovin.dispatch.management.system.model.request.CreateTrailerRequest;
import io.kovin.dispatch.management.system.model.response.TrailerData;
import io.kovin.dispatch.management.system.service.CompanyService;
import io.kovin.dispatch.management.system.service.TrailerService;
import io.kovin.dispatch.management.system.validation.TrailerValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrailerFacade {

    private final TrailerValidationService trailerValidationService;
    private final TrailerService trailerService;
    private final CompanyService companyService;

    private final TrailerMapper trailerMapper;

    @Transactional
    public TrailerData createTrailer(CreateTrailerRequest request) {
        if (request == null) {
            return new TrailerData(null);
        }

        CompanyEntity company = companyService.getByUuid(request.companyUuid());
        trailerValidationService.validateTrailerCreation(request);
        TrailerEntity trailerEntity = trailerMapper.fromCreateTrailerRequestToTrailerEntity(request, company);
        trailerService.saveTrailerEntity(trailerEntity);
        return new TrailerData(trailerEntity.getUuid());
    }
}
