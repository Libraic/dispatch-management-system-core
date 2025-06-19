package io.kovin.dispatch.management.system.utils;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_SEARCH_CRITERIA;

import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchCriteriaUtils {

    public static List<SearchCriteria> getSearchCriteriaListFromQueryParams(
        Map<String, String> queryParams
    ) {
        return queryParams.entrySet()
            .stream()
            .filter(entry -> entry.getValue() != null)
            .map(entry -> {
                String[] operationAndValue = entry.getValue().split(":");
                if (operationAndValue.length != 2) {
                    throw DispatchManagementSystemException.of(String.format(
                        INVALID_SEARCH_CRITERIA,
                        entry.getKey(),
                        entry.getValue()
                    ));
                }
                return SearchCriteria.builder()
                    .field(entry.getKey())
                    .operation(operationAndValue[0])
                    .value(operationAndValue[1])
                    .build();
            }).toList();
    }

    public static Predicate getPredicate(SearchCriteria criteria, CriteriaBuilder criteriaBuilder, Root<?> root) {
        return switch (criteria.getOperation()) {
            case "like" ->
                // We use cb.lower() to make the WHERE clause case-insensitive
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(criteria.getField())),
                    "%" + criteria.getValue().toLowerCase() + "%"
                );
            case "eq" -> criteriaBuilder.equal(
                root.get(criteria.getField()),
                criteria.getValue()
            );
            default -> null;
        };
    }
}
