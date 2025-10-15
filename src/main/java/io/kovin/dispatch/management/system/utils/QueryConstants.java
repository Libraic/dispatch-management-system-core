package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryConstants {

    /**
     * The equivalent of LIKE clause in SQL.
     */
    public static final String LIKE = "like";

    /**
     * The equivalent of = operator in SQL.
     */
    public static final String EQUAL = "eq";

    /**
     * The equivalent of INNER JOIN in SQL. Must use a pivot column to perform the join (usually using the uuid column).
     */
    public static final String JOIN = "join";

    /**
     * The equivalent of >= operator in SQL.
     */
    public static final String GREATER_OR_EQUAL = "gte";

    /**
     * The equivalent of <= operator in SQL.
     */
    public static final String LESS_OR_EQUAL = "lte";

    /**
     * The equivalent of < operator in SQL.
     */
    public static final String LESS = "lt";

    /**
     * The auditable field, createdAt, which is inherited by every entity in the system, is used as the default sorting criteria.
     */
    public static final String DEFAULT_SORTING_FIELD = "createdAt";

    /**
     * The UUID column, which is an identifier that can be used outside the database. Usually used on JOIN clauses.
     */
    public static final String UUID_FIELD = "uuid";

    /**
     * The deletedAt field.
     */
    public static final String DELETED_AT = "deletedAt";

    /**
     * Percent literal, which acts as a Wildcard in SQL.
     */
    public static final String WILDCARD = "%";

    /**
     * Default size value.
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * The CompanyEntity field name used when performing joining.
     */
    public static final String COMPANY_FIELD = "company";

    /**
     * The name of the joinable ID.
     */
    public static final String JOINABLE_FIELD_ID = "joinableEntityId";
}
