package io.kovin.dispatch.management.system.utils;

import static io.kovin.dispatch.management.system.utils.DispatchManagementSystemConstants.BLANK_STRING;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_SEARCH_CRITERIA;
import static io.kovin.dispatch.management.system.utils.LocalDateUtils.ISO_8601_FORMAT_REGEX;
import static io.kovin.dispatch.management.system.utils.LocalDateUtils.ISO_8601_LOCAL_DATE_TIME_FORMAT_REGEX;
import static io.kovin.dispatch.management.system.utils.QueryConstants.EQUAL;
import static io.kovin.dispatch.management.system.utils.QueryConstants.GREATER_OR_EQUAL;
import static io.kovin.dispatch.management.system.utils.QueryConstants.JOIN;
import static io.kovin.dispatch.management.system.utils.QueryConstants.LESS;
import static io.kovin.dispatch.management.system.utils.QueryConstants.LESS_OR_EQUAL;
import static io.kovin.dispatch.management.system.utils.QueryConstants.LIKE;
import static io.kovin.dispatch.management.system.utils.QueryConstants.UUID_FIELD;
import static io.kovin.dispatch.management.system.utils.QueryConstants.WILDCARD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public static List<SearchCriteria> getSearchCriteriaListFromQueryParams(Map<String, String> queryParams) {
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
            }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public static <S, T> Join<S, T> getJoin(Root<S> root, String joinableField) {
        return root.join(joinableField);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> Predicate getPredicate(
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
                    WILDCARD + Optional.ofNullable(criteria.getValue()).map(String::toLowerCase).orElse(BLANK_STRING) + WILDCARD
                );
            case EQUAL -> criteriaBuilder.equal(
                root.get(criteria.getField()),
                criteria.getValue()
            );
            case JOIN -> criteriaBuilder.equal(
                join.get(UUID_FIELD),
                criteria.getValue()
            );
            case GREATER_OR_EQUAL -> criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getField()), (T) parse(criteria.getValue()));
            case LESS_OR_EQUAL -> criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getField()), (T) parse(criteria.getValue()));
            case LESS -> criteriaBuilder.lessThan(root.get(criteria.getField()), (T) parse(criteria.getValue()));
            default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> T parse(String value) {
        if (value.matches(ISO_8601_FORMAT_REGEX)) {
            return (T) LocalDate.parse(value);
        } else if (value.matches(ISO_8601_LOCAL_DATE_TIME_FORMAT_REGEX)) {
            return (T) LocalDateTime.parse(value);
        }

        return (T) value;
    }
}
