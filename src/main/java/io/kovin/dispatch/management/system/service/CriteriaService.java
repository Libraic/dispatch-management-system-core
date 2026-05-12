package io.kovin.dispatch.management.system.service;

import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DEFAULT_SORTING_FIELD;
import static io.kovin.dispatch.management.system.utils.constants.QueryConstants.DELETED_AT;

import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CriteriaService {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public <T, U> Page<U> getCollection(
        List<SearchCriteria> searchCriteria,
        Class<T> clazz,
        Pageable pageable,
        Function<? super T, ? extends U> converter
    ) {
        return getCollection(searchCriteria, clazz, pageable).map(converter);
    }

    public <T> Page<T> getCollection(
        List<SearchCriteria> searchCriteria,
        Class<T> clazz,
        Pageable pageable
    ) {
        // Creates a typed query that will return results of type clazz
        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);

        // Root<T> represents the FROM clause from SQL
        Root<T> root = query.from(clazz);

        // Predicates are basically WHERE clauses
        List<Predicate> predicates = convertSearchCriteriaToPredicates(root, searchCriteria);

        // Get common predicates that apply to all entities.
        predicates.addAll(getCommonPredicates(root));
        query
            .select(root)
            .where(predicates.toArray(new Predicate[0]));

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort()
                .stream()
                .map(sortOrder ->
                    sortOrder.isAscending()
                        ? criteriaBuilder.asc(root.get(sortOrder.getProperty()))
                        : criteriaBuilder.desc(root.get(sortOrder.getProperty()))
                )
                .toList();

            query.orderBy(orders);
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(DEFAULT_SORTING_FIELD)));
        }


        List<T> content = getPaginatedContent(query, pageable);

        Long total = getTotalEntities(clazz, searchCriteria);

        return new PageImpl<>(content, pageable, total);
    }

    private <T> Long getTotalEntities(Class<T> clazz, List<SearchCriteria> searchCriteria) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(clazz);

        List<Predicate> predicates = convertSearchCriteriaToPredicates(countRoot, searchCriteria);

        predicates.addAll(getCommonPredicates(countRoot));
        countQuery
            .select(criteriaBuilder.count(countRoot))
            .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private <T> List<T> getPaginatedContent(CriteriaQuery<T> query, Pageable pageable) {
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        return typedQuery.getResultList();
    }

    private <T> List<Predicate> getCommonPredicates(Root<T> root) {
        List<Predicate> commonPredicates = new ArrayList<>();
        commonPredicates.add(criteriaBuilder.isNull(root.get(DELETED_AT)));
        return commonPredicates;
    }

    private <T> List<Predicate> convertSearchCriteriaToPredicates(Root<T> root, List<SearchCriteria> searchCriteria) {
        List<Predicate> predicates = new ArrayList<>();
        for (SearchCriteria criteria : searchCriteria) {
            Join<?, ?> join = criteria.isJoinOperation() ? SearchCriteriaUtils.getJoin(root, criteria.getField()) : null;
            Predicate predicate = SearchCriteriaUtils.getPredicate(criteria, criteriaBuilder, root, join);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        return predicates;
    }
}
