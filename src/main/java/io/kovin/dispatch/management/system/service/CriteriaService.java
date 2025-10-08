package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import static io.kovin.dispatch.management.system.utils.QueryConstants.CURSOR_FIELD;

@Service
public class CriteriaService<T> {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public List<T> getCollection(List<SearchCriteria> searchCriteria, Class<T> clazz, int size) {
        // Creates a typed query that will return results of type clazz
        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);

        // Root<T> represents the FROM clause from SQL
        Root<T> root = query.from(clazz);

        // Predicates are basically WHERE clauses
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteria) {
            Join<?, ?> join = criteria.isJoinOperation() ? SearchCriteriaUtils.getJoin(root, criteria.getField()) : null;
            Predicate predicate = SearchCriteriaUtils.getPredicate(criteria, criteriaBuilder, root, join);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        query.select(root)
            .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
            .orderBy(criteriaBuilder.desc(root.get(CURSOR_FIELD)));
        return entityManager.createQuery(query)
            .setMaxResults(size)
            .getResultList();
    }
}
