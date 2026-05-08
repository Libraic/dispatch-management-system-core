package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.DispatcherFacade;
import io.kovin.dispatch.management.system.model.request.CreateDispatcherRequest;
import io.kovin.dispatch.management.system.model.response.GetDispatcherResponse;
import io.kovin.dispatch.management.system.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.COMPANY_FIELD;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/dispatchers")
@Slf4j
public class DispatcherController {

    private final DispatcherFacade dispatcherFacade;

    @PostMapping
    public ResponseEntity<Void> createDispatcher(@RequestBody CreateDispatcherRequest createDispatcherRequest) {
        log.info("A request [{}] to create a dispatcher was received.", createDispatcherRequest);
        dispatcherFacade.createDispatcher(createDispatcherRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<GetDispatcherResponse>> getDispatchersByCriteria(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "companyId", required = false) String companyId
    ) {
        log.info("A request to fetch Dispatchers by criteria was received.");

        Map<String, String> fields = new HashMap<>();
        MapUtils.putIfNotNull(fields, "name", name);
        MapUtils.putIfNotNull(fields, COMPANY_FIELD, companyId);
        int finalPage = page == null ? 0 : page;
        int finalSize = size == null ? 0 : size;

        Page<GetDispatcherResponse> usersData = dispatcherFacade.getDispatchersByCriteria(fields, finalPage, finalSize);
        return ResponseEntity.ok(usersData);
    }
}
