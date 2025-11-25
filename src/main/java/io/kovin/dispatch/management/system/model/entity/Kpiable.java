package io.kovin.dispatch.management.system.model.entity;

/**
 * Represents an entity that can be used as a target for KPI calculations.
 * <p>
 * Implementations of this interface provide the minimal information required
 * to identify the entity, retrieve its unique identifier, and categorize it
 * by type for dynamic querying and KPI aggregation.
 */
public interface Kpiable {

    Long getId();

    String getUuid();

    String getName();

    String getEntityType();
}
