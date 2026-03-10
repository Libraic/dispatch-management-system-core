-- Liquibase formatted sql

-- changeset libra:001
-- comment: Create the uuid-ossp extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- changeset libra:002
-- comment: Create the t_companies table and its related dependencies
CREATE SEQUENCE t_companies_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_companies (
    id              BIGINT                      PRIMARY KEY DEFAULT nextval('t_companies_sequence'),
    uuid            uuid                        NOT NULL UNIQUE,
    name            VARCHAR(100)                NOT NULL,
    mc_number       VARCHAR(75),
    address         VARCHAR(255),
    service_date    DATE,
    start_date      DATE,
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE    DEFAULT NULL
);

-- Ensure the sequence is always in sync
ALTER SEQUENCE t_companies_sequence OWNED BY t_companies.id;

COMMENT ON TABLE t_companies IS 'The table used to store the companies that are used in the system.';

COMMENT ON COLUMN t_companies.id               IS 'The primary key of the t_companies table.';
COMMENT ON COLUMN t_companies.uuid             IS 'The UUID of the company.';
COMMENT ON COLUMN t_companies.name             IS 'The name of the company.';
COMMENT ON COLUMN t_companies.mc_number        IS 'The MC Number of the Company.';
COMMENT ON COLUMN t_companies.address          IS 'The physical address of the Company.';
COMMENT ON COLUMN t_companies.service_date     IS 'The date the Company was created at.';
COMMENT ON COLUMN t_companies.start_date       IS 'The date the Company started to receive services from a certain group of dispatchers.';
COMMENT ON COLUMN t_companies.created_at       IS 'The date the Company was created.';
COMMENT ON COLUMN t_companies.last_updated_at  IS 'The date the Company was last updated.';
COMMENT ON COLUMN t_companies.deleted_at       IS 'The date the Company was deleted.';

-- changeset libra:003
-- comment: Create t_dispatchers table and its related dependencies.
CREATE SEQUENCE t_dispatchers_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_dispatchers (
   id              BIGINT                   PRIMARY KEY DEFAULT nextval('t_dispatchers_sequence'),
   uuid            uuid                     NOT NULL UNIQUE,
   created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   deleted_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
   name            VARCHAR(100),
   phone_number    VARCHAR(30),
   company_id      BIGINT                   NOT NULL,

   CONSTRAINT fk_dispatcher_company FOREIGN KEY (company_id) REFERENCES t_companies(id)
);

ALTER SEQUENCE t_dispatchers_sequence OWNED BY t_dispatchers.id;

COMMENT ON TABLE t_dispatchers IS 'The table used to store the Trucks of a Company.';

COMMENT ON COLUMN t_dispatchers.id              IS 'The primary key of the t_dispatchers table.';
COMMENT ON COLUMN t_dispatchers.uuid            IS 'The UUID of the Dispatcher entity.';
COMMENT ON COLUMN t_dispatchers.created_at      IS 'The date the Dispatcher was registered.';
COMMENT ON COLUMN t_dispatchers.last_updated_at IS 'The date the Dispatcher was last updated.';
COMMENT ON COLUMN t_dispatchers.deleted_at      IS 'The date the Dispatcher was deleted.';
COMMENT ON COLUMN t_dispatchers.name            IS 'The name of the Dispatcher.';
COMMENT ON COLUMN t_dispatchers.phone_number    IS 'The phone number of the Dispatcher.';
COMMENT ON COLUMN t_dispatchers.company_id      IS 'The ID of the Company this Dispatcher works for.';

-- changeset libra:004
-- comment: Create the t_drivers table and its related dependencies
CREATE SEQUENCE t_drivers_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_drivers (
    id                        BIGINT                   PRIMARY KEY DEFAULT nextval('t_drivers_sequence'),
    uuid                      uuid                     NOT NULL UNIQUE,
    first_name                VARCHAR(100)             NOT NULL,
    last_name                 VARCHAR(100)             NOT NULL,
    full_name                 VARCHAR(200),
    phone_number              VARCHAR(30)              NOT NULL,
    email                     VARCHAR(150)             NOT NULL,
    document_status           VARCHAR(50)              NOT NULL,
    position                  VARCHAR(50)              NOT NULL,
    state                     VARCHAR(100),
    city                      VARCHAR(100),
    created_at                TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at                TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    company_id                BIGINT,
    dispatcher_id             BIGINT,

    CONSTRAINT fk_driver_company     FOREIGN KEY (company_id)    REFERENCES t_companies(id),
    CONSTRAINT fk_driver_dispatcher  FOREIGN KEY (dispatcher_id) REFERENCES t_dispatchers(id)
);

ALTER SEQUENCE t_drivers_sequence OWNED BY t_drivers.id;

COMMENT ON TABLE t_drivers IS 'The table used to store the drivers of a certain company.';

COMMENT ON COLUMN t_drivers.id                        IS 'The primary key of the t_drivers table.';
COMMENT ON COLUMN t_drivers.uuid                      IS 'The UUID of the driver.';
COMMENT ON COLUMN t_drivers.first_name                IS 'The first name of the driver.';
COMMENT ON COLUMN t_drivers.last_name                 IS 'The last name of the driver.';
COMMENT ON COLUMN t_drivers.full_name                 IS 'The combination of first name and last name of the driver.';
COMMENT ON COLUMN t_drivers.phone_number              IS 'The phone number of the driver.';
COMMENT ON COLUMN t_drivers.email                     IS 'The personal email of the driver.';
COMMENT ON COLUMN t_drivers.document_status           IS 'The legal status of the driver in the country he drives.';
COMMENT ON COLUMN t_drivers.position                  IS 'The position of the driver in the company.';
COMMENT ON COLUMN t_drivers.state                     IS 'The state the driver is currently making deliveries in.';
COMMENT ON COLUMN t_drivers.city                      IS 'The city the driver is currently making deliveries in.';
COMMENT ON COLUMN t_drivers.created_at                IS 'The date the driver was created.';
COMMENT ON COLUMN t_drivers.last_updated_at           IS 'The date the driver was last updated.';
COMMENT ON COLUMN t_drivers.deleted_at                IS 'The date the driver was deleted.';
COMMENT ON COLUMN t_drivers.company_id                IS 'The foreign key that stores the loadUuid of the company the driver works for.';
COMMENT ON COLUMN t_drivers.dispatcher_id             IS 'The ID of the Dispatcher the Driver was assigned to.';

-- changeset libra:005
-- comment: Create the t_loads table and its related dependencies
CREATE SEQUENCE t_loads_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_loads (
    id              BIGINT PRIMARY KEY DEFAULT nextval('t_loads_sequence'),
    uuid            uuid      NOT NULL UNIQUE,
    start_date      DATE      NOT NULL,
    end_date        DATE      NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    company_id      BIGINT,
    dispatcher_id   BIGINT,
    driver_id       BIGINT,
    load_data    JSONB,

    CONSTRAINT fk_load_company      FOREIGN KEY (company_id)    REFERENCES t_companies(id),
    CONSTRAINT fk_load_dispatcher   FOREIGN KEY (dispatcher_id) REFERENCES t_dispatchers(id),
    CONSTRAINT fk_load_driver       FOREIGN KEY (driver_id)     REFERENCES t_drivers(id)
);

ALTER SEQUENCE t_loads_sequence OWNED BY t_loads.id;

COMMENT ON TABLE t_loads IS 'The table used to store the loads data of Drivers from a certain Company.';

COMMENT ON COLUMN t_loads.id              IS 'The primary key of the t_loads table.';
COMMENT ON COLUMN t_loads.uuid            IS 'The UUID of the Load.';
COMMENT ON COLUMN t_loads.start_date      IS 'The start date of a particular loads.';
COMMENT ON COLUMN t_loads.end_date        IS 'The end date of a particular loads.';
COMMENT ON COLUMN t_loads.created_at      IS 'The date the Load was created.';
COMMENT ON COLUMN t_loads.last_updated_at IS 'The date the Load was last updated.';
COMMENT ON COLUMN t_loads.deleted_at      IS 'The date the Load was deleted.';
COMMENT ON COLUMN t_loads.company_id      IS 'The ID of the Company the Driver works for.';
COMMENT ON COLUMN t_loads.driver_id       IS 'The ID of the Driver.';
COMMENT ON COLUMN t_drivers.dispatcher_id           IS 'The ID of the Dispatcher.';
COMMENT ON COLUMN t_loads.load_data    IS 'The data that describes the Load.';

-- changeset libra:006
-- comment: Create the t_trailers table and its related dependencies
CREATE SEQUENCE t_trailers_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_trailers (
    id              BIGINT    PRIMARY KEY DEFAULT nextval('t_trailers_sequence'),
    uuid            uuid      NOT NULL UNIQUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    trailer_number  VARCHAR   NOT NULL,
    vin_number      VARCHAR   NOT NULL UNIQUE,
    trailer_year    SMALLINT,
    trailer_make    VARCHAR,
    equipment_type  VARCHAR,
    equipment_size  INTEGER,
    pallet_capacity INTEGER,
    max_weight      INTEGER,
    tire_size       VARCHAR,
    company_id      BIGINT,

    CONSTRAINT fk_trailer_company FOREIGN KEY (company_id) REFERENCES t_companies(id)
);

ALTER SEQUENCE t_trailers_sequence OWNED BY t_trailers.id;

COMMENT ON TABLE t_trailers IS 'The table used to store the Trailers of a Company.';

COMMENT ON COLUMN t_trailers.id              IS 'The primary key of the t_trailers table.';
COMMENT ON COLUMN t_trailers.uuid            IS 'The UUID of the Trailer.';
COMMENT ON COLUMN t_trailers.created_at      IS 'The date the Trailer was registered.';
COMMENT ON COLUMN t_trailers.last_updated_at IS 'The date the Trailer was last updated.';
COMMENT ON COLUMN t_trailers.deleted_at      IS 'The date the Trailer was deleted.';
COMMENT ON COLUMN t_trailers.trailer_number  IS 'The Trailer number.';
COMMENT ON COLUMN t_trailers.vin_number      IS 'The VIN number of the Trailer.';
COMMENT ON COLUMN t_trailers.trailer_year    IS 'The year the Trailer was manufactured.';
COMMENT ON COLUMN t_trailers.trailer_make    IS 'The manufacturer of the Trailer.';
COMMENT ON COLUMN t_trailers.equipment_type  IS 'The equipment type of the Trailer.';
COMMENT ON COLUMN t_trailers.equipment_size  IS 'The equipment size of the Trailer.';
COMMENT ON COLUMN t_trailers.pallet_capacity IS 'The pallet capacity of the Trailer.';
COMMENT ON COLUMN t_trailers.max_weight      IS 'The max weight the Trailer is allowed to carry.';
COMMENT ON COLUMN t_trailers.tire_size       IS 'The tire size of the Trailer.';
COMMENT ON COLUMN t_trailers.company_id      IS 'The ID of the Company this trailer was created for.';

-- changeset libra:007
-- comment: Create the t_trucks table and its related dependencies
CREATE SEQUENCE t_trucks_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_trucks (
    id              BIGINT PRIMARY KEY DEFAULT nextval('t_trucks_sequence'),
    uuid            uuid   NOT NULL UNIQUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    truck_number    VARCHAR NOT NULL,
    vin_number      VARCHAR NOT NULL UNIQUE,
    model           VARCHAR,
    truck_year      SMALLINT,
    truck_make      VARCHAR,
    fuel_type       VARCHAR,
    color           VARCHAR,
    weight          INTEGER,
    company_id      BIGINT,

    CONSTRAINT fk_truck_company FOREIGN KEY (company_id) REFERENCES t_companies(id)
);

ALTER SEQUENCE t_trucks_sequence OWNED BY t_trucks.id;

COMMENT ON TABLE t_trucks IS 'The table used to store the Trucks of a Company.';

COMMENT ON COLUMN t_trucks.id              IS 'The primary key of the t_trucks table.';
COMMENT ON COLUMN t_trucks.uuid            IS 'The UUID of the Truck.';
COMMENT ON COLUMN t_trucks.created_at      IS 'The date the Truck was registered.';
COMMENT ON COLUMN t_trucks.last_updated_at IS 'The date the Truck was last updated.';
COMMENT ON COLUMN t_trucks.deleted_at      IS 'The date the Truck was deleted.';
COMMENT ON COLUMN t_trucks.truck_number    IS 'The Truck number.';
COMMENT ON COLUMN t_trucks.vin_number      IS 'The VIN number of the Truck.';
COMMENT ON COLUMN t_trucks.model           IS 'The model of the Truck.';
COMMENT ON COLUMN t_trucks.truck_year      IS 'The year the Truck was manufactured.';
COMMENT ON COLUMN t_trucks.truck_make      IS 'The manufacturer of the Truck.';
COMMENT ON COLUMN t_trucks.fuel_type       IS 'The fuel type of the Truck.';
COMMENT ON COLUMN t_trucks.color           IS 'The color of the Truck.';
COMMENT ON COLUMN t_trucks.weight          IS 'The weight the Truck is allowed to carry.';
COMMENT ON COLUMN t_trucks.company_id      IS 'The ID of the Company this truck was created for.';

-- changeset libra:008
-- comment: Add truck_id (t_trucks table) and trailer_id (t_trailers table) as foreign keys to the t_drivers table.
ALTER TABLE t_drivers
    ADD COLUMN truck_id   BIGINT,
    ADD COLUMN trailer_id BIGINT,

    ADD CONSTRAINT fk_driver_truck   FOREIGN KEY (truck_id)   REFERENCES t_trucks(id),
    ADD CONSTRAINT fk_driver_trailer FOREIGN KEY (trailer_id) REFERENCES t_trailers(id);

COMMENT ON COLUMN t_drivers.truck_id   IS 'The ID of the Truck that is currently assigned to this Driver.';
COMMENT ON COLUMN t_drivers.trailer_id IS 'The ID of the Trailer that is currently assigned to this Driver.';
