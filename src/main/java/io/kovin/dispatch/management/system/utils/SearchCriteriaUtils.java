package io.kovin.dispatch.management.system.utils;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_SEARCH_CRITERIA;
import static io.kovin.dispatch.management.system.utils.LocalDateUtils.LITTLE_ENDIAN_FORMAT_REGEX;
import static io.kovin.dispatch.management.system.utils.SearchOperations.EQUAL;
import static io.kovin.dispatch.management.system.utils.SearchOperations.GREATER_OR_EQUAL;
import static io.kovin.dispatch.management.system.utils.SearchOperations.JOIN;
import static io.kovin.dispatch.management.system.utils.SearchOperations.LESS_OR_EQUAL;
import static io.kovin.dispatch.management.system.utils.SearchOperations.LIKE;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
                    String message = String.format(INVALID_SEARCH_CRITERIA, entry.getKey(), entry.getValue());
                    throw DispatchManagementSystemException.of(message, HttpStatus.BAD_REQUEST);
                }
                return SearchCriteria.builder()
                    .field(entry.getKey())
                    .operation(operationAndValue[0])
                    .value(operationAndValue[1])
                    .build();
            }).toList();
    }

    public static <S, T> Join<S, T> getJoin(Root<S> root, String joinableField) {
        return root.join(joinableField);
    }

    public static Predicate getPredicate(
        SearchCriteria criteria,
        CriteriaBuilder criteriaBuilder,
        Root<?> root,
        Join<?, ?> join
    ) {
        return switch (criteria.getOperation()) {
            case LIKE ->
                // We use cb.lower() to make the WHERE clause case-insensitive
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(criteria.getField())),
                    "%" + criteria.getValue().toLowerCase() + "%"
                );
            case EQUAL -> criteriaBuilder.equal(
                root.get(criteria.getField()),
                criteria.getValue()
            );
            case JOIN -> criteriaBuilder.equal(
                join.get("uuid"),
                criteria.getValue()
            );
            case GREATER_OR_EQUAL -> {
                String value = criteria.getValue();
                if (value.matches(LITTLE_ENDIAN_FORMAT_REGEX)) {
                    LocalDate dateValue = LocalDateUtils.parseLocalDate(value);
                    yield criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getField()), dateValue);
                }
                yield null;
            }
            case LESS_OR_EQUAL -> {
                String value = criteria.getValue();
                if (value.matches(LITTLE_ENDIAN_FORMAT_REGEX)) {
                    LocalDate dateValue = LocalDateUtils.parseLocalDate(value);
                    yield criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getField()), dateValue);
                }
                yield null;
            }
            default -> null;
        };
    }
}
