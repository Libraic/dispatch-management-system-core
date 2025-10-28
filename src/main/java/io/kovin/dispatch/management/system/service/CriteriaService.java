package io.kovin.dispatch.management.system.service;

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
import org.springframework.stereotype.Service;

import static io.kovin.dispatch.management.system.utils.ErrorMessage.INVALID_PAGEABLE_ENTITY;
import static io.kovin.dispatch.management.system.utils.QueryConstants.COMPANY_FIELD;
import static io.kovin.dispatch.management.system.utils.QueryConstants.DEFAULT_SIZE;
import static io.kovin.dispatch.management.system.utils.QueryConstants.DEFAULT_SORTING_FIELD;
import static io.kovin.dispatch.management.system.utils.QueryConstants.DELETED_AT;
import static io.kovin.dispatch.management.system.utils.QueryConstants.UUID_FIELD;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class CriteriaService {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

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

        // Get common Predicates, that apply to all entities.
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

    public PaginationDetails getPaginationDetails(String pageableEntity, String joinableEntityId, Integer pageSize) {
        Class<?> entityType = PageableEntity.getClass(pageableEntity);
        if (entityType == null) {
            throw DispatchManagementSystemException.of(INVALID_PAGEABLE_ENTITY.formatted(pageableEntity), BAD_REQUEST);
        }

        long numberOfRecords = count(entityType, joinableEntityId, COMPANY_FIELD);
        return SearchCriteriaUtils.getPaginationDetails(numberOfRecords, pageSize);
    }

    public <T> long count(Class<T> clazz, String joinableEntityId, String joinableEntityName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        Join<?, ?> join = SearchCriteriaUtils.getJoin(root, joinableEntityName);
        Predicate joinPredicate = criteriaBuilder.equal(join.get(UUID_FIELD), joinableEntityId);
        query.select(cb.count(root)).where(criteriaBuilder.and(joinPredicate));
        return entityManager.createQuery(query).getSingleResult();
    }

    private <T> List<Predicate> getCommonPredicates(Root<T> root) {
        List<Predicate> commonPredicates = new ArrayList<>();
        commonPredicates.add(criteriaBuilder.isNull(root.get(DELETED_AT)));
        return commonPredicates;
    }
}
