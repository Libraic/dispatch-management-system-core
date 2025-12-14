package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.model.response.PaginationDetails;
import io.kovin.dispatch.management.system.service.CriteriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.JOINABLE_FIELD_ID_REQUEST_PARAM;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.JOINABLE_FIELD_NAME_REQUEST_PARAM;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.PAGEABLE_ENTITY_REQUEST_PARAM;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/core")
public class CoreController {

    private final CriteriaService criteriaService;

    @GetMapping("/pagination")
    public ResponseEntity<PaginationDetails> getPaginationDetails(
        @RequestParam(name = PAGEABLE_ENTITY_REQUEST_PARAM) String pageableEntity,
        @RequestParam(name = JOINABLE_FIELD_ID_REQUEST_PARAM, required = false) String joinableEntityId,
        @RequestParam(name = JOINABLE_FIELD_NAME_REQUEST_PARAM, required = false) String joinableEntityName,
        @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        log.info("A request to get the Pagination Details about [{}] entity was received.", pageableEntity);
        PaginationDetails paginationDetails = criteriaService.getPaginationDetails(
            pageableEntity,
            joinableEntityId,
            joinableEntityName,
            pageSize
        );
        return ResponseEntity.ok(paginationDetails);
    }
}
