package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_PAGEABLE_ENTITY;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SIZE;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DELETED_AT;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.UUID;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import io.kovin.dispatch.management.system.exception.DispatchManagementSystemException;
import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.model.request.enums.PageableEntity;
import io.kovin.dispatch.management.system.model.response.PaginationDetails;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CriteriaService {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    /**
     * Executes a dynamic, type-safe Criteria API query on the specified entity class and returns
     * a paginated list of matching results.
     * <p>
     * This method supports:
     * <ul>
     *   <li>dynamic filtering based on a list of {@link SearchCriteria}</li>
     *   <li>automatic detection and handling of join operations for criteria involving related entities</li>
     *   <li>application of common predicates shared across all entity types</li>
     *   <li>sorting of results in descending order based on the default sorting field</li>
     *   <li>pagination using the provided {@code page} and {@code size} parameters</li>
     * </ul>
     *
     * @param searchCriteria the list of filter conditions used to construct WHERE predicates;
     *                       may include join-based filters
     * @param clazz          the entity class to query; defines the result type of the returned list
     * @param page           the zero-based page index used for pagination
     * @param size           the number of items per page; if {@code 0}, a default size is applied
     * @param <T>            the type of entity returned by the query
     *
     * @return a paginated list of entities of type {@code T} matching the filters;
     *         returns an empty list if no results match
     *
     * @throws IllegalArgumentException if {@code clazz} is null
     */
    public <T> List<T> getCollection(List<SearchCriteria> searchCriteria, Class<T> clazz, int page, int size) {
        // Creates a typed query that will return results of type clazz
        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);

        // Root<T> represents the FROM clause from SQL
        Root<T> root = query.from(clazz);

        // Predicates are basically WHERE clauses
        List<Predicate> predicates = new ArrayList<>();

        // Create Predicates from the Query Parameters
        for (SearchCriteria criteria : searchCriteria) {
            Join<?, ?> join = criteria.isJoinOperation() ? SearchCriteriaUtils.getJoin(root, criteria.getField()) : null;
            Predicate predicate = SearchCriteriaUtils.getPredicate(criteria, criteriaBuilder, root, join);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        // Get common predicates that apply to all entities.
        predicates.addAll(getCommonPredicates(root));

        query.select(root)
            .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
            .orderBy(criteriaBuilder.desc(root.get(DEFAULT_SORTING_FIELD)));

        int finalSize = size == 0 ? DEFAULT_SIZE : size;
        TypedQuery<T> typedQuery = entityManager.createQuery(query)
            .setFirstResult(page * finalSize)
            .setMaxResults(finalSize);
        return typedQuery.getResultList();
    }

    /**
     * Determines the pagination details for the specified pageable entity, optionally filtered by a related entity.
     * <p>
     * This method determines the total number of records for the given entity type (optionally filtered by a
     * related entity) and delegates to {@link SearchCriteriaUtils#getPaginationDetails(long, Integer)} to
     * compute page count, offsets, and other pagination metadata.
     *
     * @param pageableEntity    the name of the entity to paginate; must correspond to a valid entity type
     * @param joinableEntityId  the loadUuid of a related entity to filter by; may be {@code null} if no filtering is required
     * @param joinableEntityName the name of the relationship field to join on when filtering; must be provided if {@code joinableEntityId} is non-null
     * @param pageSize          the number of items per page; may be {@code null} to use a default page size
     * @return a {@link PaginationDetails} object containing total records, page count, and related pagination metadata
     *
     * @throws DispatchManagementSystemException if {@code pageableEntity} does not correspond to a valid entity type
     */
    public PaginationDetails getPaginationDetails(
        String pageableEntity,
        UUID joinableEntityId,
        String joinableEntityName,
        Integer pageSize
    ) {
        Class<?> entityType = PageableEntity.getClass(pageableEntity);
        if (entityType == null) {
            throw DispatchManagementSystemException.of(INVALID_PAGEABLE_ENTITY.formatted(pageableEntity), BAD_REQUEST);
        }

        long numberOfRecords = count(entityType, joinableEntityId, joinableEntityName);
        return SearchCriteriaUtils.getPaginationDetails(numberOfRecords, pageSize);
    }

    /**
     * Counts the number of entities of the specified type, optionally filtering by a related entity.
     * <p>
     * This method performs a type-safe Criteria API query to count all instances of the given entity class.
     * If both {@code joinableEntityId} and {@code joinableEntityName} are provided, the count
     * is restricted to entities that are associated with the specified related entity through a join.
     *
     * @param clazz              the entity class to count; defines the type of entities
     * @param joinableEntityId   the loadUuid of the related entity to filter by; if null, no join filter is applied
     * @param joinableEntityName the name of the relationship field to join on; required if {@code joinableEntityId} is provided
     * @param <T>                the type of entity to count
     * @return the total number of entities of type {@code T} that match the optional join filter
     *
     * @throws IllegalArgumentException if {@code clazz} is null
     */
    public <T> long count(Class<T> clazz, UUID joinableEntityId, String joinableEntityName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        query.select(cb.count(root));
        if (joinableEntityId != null && joinableEntityName != null) {
            Join<?, ?> join = SearchCriteriaUtils.getJoin(root, joinableEntityName);
            Predicate joinPredicate = criteriaBuilder.equal(join.get(UUID), joinableEntityId);
            query.where(criteriaBuilder.and(joinPredicate));
        }

        return entityManager.createQuery(query).getSingleResult();
    }

    private <T> List<Predicate> getCommonPredicates(Root<T> root) {
        List<Predicate> commonPredicates = new ArrayList<>();
        commonPredicates.add(criteriaBuilder.isNull(root.get(DELETED_AT)));
        return commonPredicates;
    }
}
