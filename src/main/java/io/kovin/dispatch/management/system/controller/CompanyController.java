package io.kovin.dispatch.management.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.facade.CompanyFacade;
import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.model.response.error.ErrorResponse;
import io.kovin.dispatch.management.system.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final CompanyFacade companyFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyData, ErrorResponse>> createCompany(@RequestBody CreateCompanyRequest createCompanyRequest) {
        log.info("A request to create a company with name=[{}] was received.", createCompanyRequest.name());
        CompanyData companyData = companyFacade.saveCompany(createCompanyRequest);
        ApiResponse<CompanyData, ErrorResponse> apiResponse = ApiResponse.fromData(companyData);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<CompanyData, ErrorResponse>> getByUuid(@PathVariable String uuid) {
        log.info("A request to retrieve the company with UUID=[{}] was received.", uuid);
        CompanyEntity companyEntity = companyService.getByUuid(uuid);
        CompanyData companyData = companyMapper.fromCompanyEntityToCompanyData(companyEntity);
        ApiResponse<CompanyData, ErrorResponse> apiResponse = ApiResponse.fromData(companyData);
        return ResponseEntity.ok(apiResponse);
    }

//    public ResponseEntity<ApiResponse<PagedModel<EntityModel<CompanyData>>>> getCompanies(
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "1") int size
//    ) {
//        log.info("A request to retrieve all companies was received.");
//        GetCompaniesRequest getCompaniesRequest = GetCompaniesRequest.builder()
//            .page(page)
//            .size(size)
//            .build();
//        PagedModel<EntityModel<CompanyData>> companyDataPage = companyFacade.getCompanies(getCompaniesRequest);
//        ApiResponse<PagedModel<EntityModel<CompanyData>>> apiResponse = ApiResponse.fromData(companyDataPage);
//        return ResponseEntity.ok(apiResponse);
//    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyData>, ErrorResponse>> getCompaniesByCriteria(
        @RequestParam(name = "uuid", required = false) String uuid,
        @RequestParam(name = "name", required = false) String name
    ) {
        log.info("A request to retrieve the companies by criteria was received.");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("uuid", uuid);
        queryParams.put("name", name);
        List<CompanyEntity> companies = companyService.getCompanies(queryParams);
        List<CompanyData> companiesData = companies.stream()
            .map(companyMapper::fromCompanyEntityToCompanyData)
            .toList();
        return ResponseEntity.ok(ApiResponse.fromData(companiesData));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<CompanyData, ErrorResponse>> deleteCompany(@PathVariable String uuid) {
        log.info("A request to delete the company with UUID=[{}] was received.", uuid);
        companyService.deleteCompany(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).build();
    }
 }
