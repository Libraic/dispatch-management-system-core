package io.kovin.dispatch.management.system.service;

import io.kovin.dispatch.management.system.model.criteria.SearchCriteria;
import io.kovin.dispatch.management.system.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CriteriaService<T> {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public List<T> getCollection(List<SearchCriteria> searchCriteria, Class<T> clazz) {

        // Creates a typed query that will return results of type clazz
        CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);

        // Root<T> represents the FROM clause from SQL
        Root<T> root = query.from(clazz);

        // Predicates are basically WHERE clauses
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteria) {
            Predicate predicate = SearchCriteriaUtils.getPredicate(criteria, criteriaBuilder, root);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        query.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
