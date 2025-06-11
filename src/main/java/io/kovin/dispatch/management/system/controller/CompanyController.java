package io.kovin.dispatch.management.system.controller;

import io.kovin.dispatch.management.system.mapper.CompanyMapper;
import io.kovin.dispatch.management.system.model.entity.CompanyEntity;
import io.kovin.dispatch.management.system.model.request.CreateCompanyRequest;
import io.kovin.dispatch.management.system.model.response.ApiResponse;
import io.kovin.dispatch.management.system.model.response.CompanyData;
import io.kovin.dispatch.management.system.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/company")
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:5173")
@Slf4j
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyData>> createCompany(@RequestBody CreateCompanyRequest createCompanyRequest) {
        log.info("A request to create a company with name=[{}] was received.", createCompanyRequest.name());
        CompanyEntity companyEntity = companyService.createCompany(createCompanyRequest);
        CompanyData companyData = companyMapper.fromCompanyEntityToCompanyData(companyEntity);
        ApiResponse<CompanyData> apiResponse = ApiResponse.fromData(companyData);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<CompanyData>> getByUuid(@PathVariable String uuid) {
        log.info("A request to retrieve the company with UUID=[{}] was received.", uuid);
        CompanyEntity companyEntity = companyService.getByUuid(uuid);
        CompanyData companyData = companyMapper.fromCompanyEntityToCompanyData(companyEntity);
        ApiResponse<CompanyData> apiResponse = ApiResponse.fromData(companyData);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyData>>> getCompanies() {
        log.info("A request to retrieve all companies was received.");
        List<CompanyEntity> companyEntities = companyService.getCompanies();
        List<CompanyData> companyDataList = companyMapper.fromCompanyEntityListToCompanyDataList(companyEntities);
        ApiResponse<List<CompanyData>> apiResponse = ApiResponse.fromData(companyDataList);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<CompanyData>> deleteCompany(@PathVariable String uuid) {
        log.info("A request to delete the company with UUID=[{}] was received.", uuid);
        companyService.deleteCompany(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).build();
    }
 }
