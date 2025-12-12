package io.kovin.dispatch.management.system.utils;

import static io.kovin.dispatch.management.system.utils.constants.DispatchManagementSystemConstants.BLANK_STRING;
import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_SEARCH_CRITERIA;
import static io.kovin.dispatch.management.system.utils.LocalDateUtils.ISO_8601_FORMAT_REGEX;
import static io.kovin.dispatch.management.system.utils.LocalDateUtils.ISO_8601_LOCAL_DATE_TIME_FORMAT_REGEX;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.EQUAL;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.GREATER_OR_EQUAL;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.JOIN;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.LESS;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.LESS_OR_EQUAL;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.LIKE;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.UUID;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.WILDCARD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.response.PaginationDetails;
import io.kovin.dispatch.management.system.utils.constants.QueryConstants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchCriteriaUtils {

    /**
     * Converts the String literals, namely the left-hand operator, the operation and the right-hand operator into
     * a DTO object.
     *
     * @param queryParams the map containing the String literals, where the K is the field, and V is the op:value.
     * @return a list of DTOs.
     */
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

    /**
     * Converts the conditions/filters of searching into processable Predicates by Criteria API.
     * If there is a join object sent, it will be considered.
     *
     * @param criteria        the DTO containing the conditions and the fields involved.
     * @param criteriaBuilder the Criteria Builder object.
     * @param root            the Root of the query.
     * @param join            the object depicting the JOIN clause.
     * @return                the Predicate that reflects the condition/filter to be applied.
     * @param <T>             the type of the entity.
     */
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
            case JOIN -> {
                if (join != null) {
                    yield criteriaBuilder.equal(join.get(UUID), criteria.getValue());
                }
                yield null;
            }
            case GREATER_OR_EQUAL -> criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getField()), (T) parse(criteria.getValue()));
            case LESS_OR_EQUAL -> criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getField()), (T) parse(criteria.getValue()));
            case LESS -> criteriaBuilder.lessThan(root.get(criteria.getField()), (T) parse(criteria.getValue()));
            default -> null;
        };
    }

    /**
     * Return the number of pages it would take to display all the records.
     *
     * @param numberOfRecords the total number of records.
     * @param pageSize        the number of records on a single page.
     * @return                the number of pages required to contain all the records.
     */
    public static PaginationDetails getPaginationDetails(long numberOfRecords, Integer pageSize) {
        int size = pageSize == null ? QueryConstants.DEFAULT_SIZE : pageSize;
        long numberOfPages = (numberOfRecords % size != 0 ? 1 : 0) + (numberOfRecords / size);
        return new PaginationDetails(numberOfRecords, numberOfPages);
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
