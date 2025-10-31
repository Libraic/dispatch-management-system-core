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
COMMENT ON COLUMN t_companies.created_at       IS 'The date the company was created.';
COMMENT ON COLUMN t_companies.last_updated_at  IS 'The date the company was last updated.';
COMMENT ON COLUMN t_companies.deleted_at       IS 'The date the company was deleted.';

-- changeset libra:003
-- comment: Create the t_users table and its related dependencies
CREATE SEQUENCE t_users_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_users (
    id BIGINT       PRIMARY                     KEY DEFAULT nextval('t_users_sequence'),
    uuid uuid       NOT NULL                    UNIQUE,
    first_name      VARCHAR(50)                 NOT NULL,
    last_name       VARCHAR(50)                 NOT NULL,
    role            VARCHAR(50)                 NOT NULL,
    position        VARCHAR(50)                 NOT NULL,
    birth_date      DATE                        NOT NULL,
    email           VARCHAR(60)                 NOT NULL UNIQUE,
    password        VARCHAR                     NOT NULL,
    personal_email  VARCHAR(60),
    employment_date DATE                        NOT NULL,
    dismissal_date  DATE,
    created_at      TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE    DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE    DEFAULT NULL,
    supervisor_id   BIGINT,

    CONSTRAINT fk_supervisor FOREIGN KEY (supervisor_id) REFERENCES t_users(id)
);

ALTER SEQUENCE t_users_sequence OWNED BY t_users.id;

COMMENT ON TABLE t_users IS 'The table used to store the users of the application.';

COMMENT ON COLUMN t_users.id               IS 'The primary key of the t_users table.';
COMMENT ON COLUMN t_users.uuid             IS 'The UUID of the user.';
COMMENT ON COLUMN t_users.first_name       IS 'The first name of the user.';
COMMENT ON COLUMN t_users.last_name        IS 'The last name of the user.';
COMMENT ON COLUMN t_users.role             IS 'The role in the system of the user.';
COMMENT ON COLUMN t_users.position         IS 'The functional position of the user.';
COMMENT ON COLUMN t_users.birth_date       IS 'The birth date of the user.';
COMMENT ON COLUMN t_users.email            IS 'The e-mail (which acts as the username in the authentication process) of the user.';
COMMENT ON COLUMN t_users.password         IS 'The hashed password of the user.';
COMMENT ON COLUMN t_users.personal_email   IS 'The personal e-mail of the user (that is not provided by the dispatching company).';
COMMENT ON COLUMN t_users.employment_date  IS 'The date the user was employed in the dispatching company.';
COMMENT ON COLUMN t_users.dismissal_date   IS 'The date the user was dismissed from the dispatching company.';
COMMENT ON COLUMN t_users.created_at       IS 'The date the user was created.';
COMMENT ON COLUMN t_users.last_updated_at  IS 'The date the user was last updated.';
COMMENT ON COLUMN t_users.deleted_at       IS 'The date the user was deleted.';
COMMENT ON COLUMN t_users.supervisor_id    IS 'The foreign key that stored the identifier of the user that supervises the current user.';

-- changeset libra:004
-- comment: Create the t_usercompanies table

CREATE TABLE t_usercompanies (
    user_id    BIGINT         NOT NULL,
    company_id BIGINT         NOT NULL,
    commission DECIMAL(10, 6),

    CONSTRAINT pk_user_company PRIMARY KEY (user_id, company_id),

    CONSTRAINT fk_user         FOREIGN KEY (user_id)             REFERENCES t_users(id),
    CONSTRAINT fk_company      FOREIGN KEY (company_id)          REFERENCES t_companies(id)
);

COMMENT ON TABLE t_usercompanies IS 'The table that stores the many-to-many relationship between t_users and t_companies';

COMMENT ON COLUMN t_usercompanies.user_id    IS 'The foreign key that stores the identifier of the User.';
COMMENT ON COLUMN t_usercompanies.company_id IS 'The foreign key that stores the identifier of the Company.';
COMMENT ON COLUMN t_usercompanies.commission IS 'The commission that a User takes from providing services for the Company.';

-- changeset libra:005
-- comment: Create the t_notes table and its related dependencies

CREATE SEQUENCE t_notes_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_notes (
    id          BIGINT  PRIMARY KEY DEFAULT nextval('t_notes_sequence'),
    uuid        uuid    NOT NULL UNIQUE,
    description VARCHAR NOT NULL,
    user_id     BIGINT,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES t_users(id)
);

ALTER SEQUENCE t_notes_sequence OWNED BY t_notes.id;

COMMENT ON TABLE t_notes IS 'The table used to store the notes of the User.';

COMMENT ON COLUMN t_notes.id          IS 'The primary key of the t_notes table.';
COMMENT ON COLUMN t_notes.uuid        IS 'The UUID of the note.';
COMMENT ON COLUMN t_notes.description IS 'The text of the note.';
COMMENT ON COLUMN t_notes.user_id     IS 'The user the current note pertains to.';

-- changeset libra:006
-- comment: Add nickname column to t_users table
ALTER TABLE t_users ADD COLUMN nickname VARCHAR(100);

COMMENT ON COLUMN t_users.nickname IS 'The nickname of the user.';

-- changeset libra:007
-- comment: Rename description column from t_notes table to content
ALTER TABLE t_notes RENAME COLUMN description TO content;

COMMENT ON COLUMN t_notes.content IS 'The content of the note.';

-- changeset libra:008
-- comment: Add full_name column to t_users table
ALTER TABLE t_users ADD COLUMN full_name VARCHAR(200);

COMMENT ON COLUMN t_users.full_name IS 'The combination of first name, nickname and last name of the user.';

-- changeset libra:009
-- comment: Add mc_number, address and service_date columns to t_companies table
ALTER TABLE t_companies
    ADD COLUMN mc_number    VARCHAR(75),
    ADD COLUMN address      VARCHAR(255),
    ADD COLUMN service_date DATE;

COMMENT ON COLUMN t_companies.mc_number    IS 'The MC Number of the Company.';
COMMENT ON COLUMN t_companies.address      IS 'The physical address of the Company.';
COMMENT ON COLUMN t_companies.service_date IS 'The date the Company was created at.';

-- changeset libra:010
-- comment: Add name, relationship and phone_number columns to t_users table
ALTER TABLE t_users
    ADD COLUMN emergency_contact_name         VARCHAR(75),
    ADD COLUMN emergency_contact_relationship VARCHAR(50),
    ADD COLUMN emergency_contact_phone_number VARCHAR(40);

COMMENT ON COLUMN t_users.emergency_contact_name         IS 'The name of the person added as the emergency contact.';
COMMENT ON COLUMN t_users.emergency_contact_relationship IS 'The relationship between the person added as the emergency contact and the user.';
COMMENT ON COLUMN t_users.emergency_contact_phone_number IS 'The phone number of the person added as an emergency contact.'

-- changeset libra:011
-- comment: Add start_date column to t_companies table
ALTER TABLE t_companies
    ADD COLUMN start_date DATE;

COMMENT ON COLUMN t_companies.start_date IS 'The date the Company started to receive services from a certain group of dispatchers.';

-- changeset libra:012
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
    phone_number              VARCHAR(30)              NOT NULL,
    email                     VARCHAR(150)             NOT NULL,
    truck_number              VARCHAR(50)              NOT NULL,
    trailer_number            VARCHAR(50)              NOT NULL,
    max_legal_weight_capacity DECIMAL(10, 2)           NOT NULL,
    trailer_type              VARCHAR(50)              NOT NULL,
    trailer_length            DECIMAL(10, 2)           NOT NULL,
    document_status           VARCHAR(50)              NOT NULL,
    position                  VARCHAR(50)              NOT NULL,
    created_at                TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at                TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    company_id                BIGINT,

    CONSTRAINT fk_company     FOREIGN KEY (company_id) REFERENCES t_companies(id)
);

ALTER SEQUENCE t_drivers_sequence OWNED BY t_drivers.id;

COMMENT ON TABLE t_drivers IS 'The table used to store the drivers of a certain company.';

COMMENT ON COLUMN t_drivers.id                        IS 'The primary key of the t_drivers table.';
COMMENT ON COLUMN t_drivers.uuid                      IS 'The UUID of the driver.';
COMMENT ON COLUMN t_drivers.first_name                IS 'The first name of the driver.';
COMMENT ON COLUMN t_drivers.last_name                 IS 'The last name of the driver.';
COMMENT ON COLUMN t_drivers.phone_number              IS 'The phone number of the driver.';
COMMENT ON COLUMN t_drivers.email                     IS 'The personal email of the driver.';
COMMENT ON COLUMN t_drivers.truck_number              IS 'The number of the truck the driver drives.';
COMMENT ON COLUMN t_drivers.trailer_number            IS 'The number of trailer attached to the truck.';
COMMENT ON COLUMN t_drivers.max_legal_weight_capacity IS 'The maximum legally-allowed capacity that can be carried.';
COMMENT ON COLUMN t_drivers.trailer_type              IS 'The type of the trailer.';
COMMENT ON COLUMN t_drivers.trailer_length            IS 'The length of the trailer.';
COMMENT ON COLUMN t_drivers.document_status           IS 'The legal status of the driver in the country he drives.';
COMMENT ON COLUMN t_drivers.position                  IS 'The position of the driver in the company.';
COMMENT ON COLUMN t_drivers.created_at                IS 'The date the driver was created.';
COMMENT ON COLUMN t_drivers.last_updated_at           IS 'The date the driver was last updated.';
COMMENT ON COLUMN t_drivers.deleted_at                IS 'The date the driver was deleted.';
COMMENT ON COLUMN t_drivers.company_id                IS 'The foreign key that stores the identifier of the company the driver works for.';

-- changeset libra:013
-- comment: Add the full_name column to t_drivers table
ALTER TABLE t_drivers ADD COLUMN full_name VARCHAR(200);

COMMENT ON COLUMN t_drivers.full_name IS 'The combination of first name and last name of the driver.';

-- changeset libra:014
-- comment: Add the state and city column to t_drivers table
ALTER TABLE t_drivers
    ADD COLUMN state VARCHAR(100),
    ADD COLUMN city  VARCHAR(100);

COMMENT ON COLUMN t_drivers.state IS 'The state the driver is currently making deliveries in.';
COMMENT ON COLUMN t_drivers.city  IS 'The city the driver is currently making deliveries in.';

-- changeset libra:015
-- comment: Add the height of the truck
ALTER TABLE t_drivers
    ADD COLUMN trailer_height DECIMAL(10, 2);

COMMENT ON COLUMN t_drivers.trailer_height IS 'The height of the trailer.';

-- changeset libra:016
-- comment: Create the t_drivers_mileage table and its related dependencies
CREATE SEQUENCE t_drivers_mileage_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_drivers_mileage (
    id              BIGINT PRIMARY KEY DEFAULT nextval('t_drivers_mileage_sequence'),
    uuid            uuid   NOT NULL UNIQUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    dispatcher_id   BIGINT,
    company_id      BIGINT,
    driver_id       BIGINT,
    mileage_data    JSONB,

    CONSTRAINT fk_mileage_dispatcher   FOREIGN KEY (dispatcher_id) REFERENCES t_users(id),
    CONSTRAINT fk_mileage_company      FOREIGN KEY (company_id)    REFERENCES t_companies(id),
    CONSTRAINT fk_mileage_driver       FOREIGN KEY (driver_id)     REFERENCES t_drivers(id)
);

ALTER SEQUENCE t_drivers_mileage_sequence OWNED BY t_drivers_mileage.id;

COMMENT ON TABLE t_drivers_mileage IS 'The table used to store the mileage data of Drivers from a certain Company.';

COMMENT ON COLUMN t_drivers_mileage.id              IS 'The primary key of the t_drivers_mileage table.';
COMMENT ON COLUMN t_drivers_mileage.uuid            IS 'The UUID of the Driver Mileage.';
COMMENT ON COLUMN t_drivers_mileage.created_at      IS 'The date the Driver Mileage was created.';
COMMENT ON COLUMN t_drivers_mileage.last_updated_at IS 'The date the Driver Mileage was last updated.';
COMMENT ON COLUMN t_drivers_mileage.deleted_at      IS 'The date the Driver Mileage was deleted.';
COMMENT ON COLUMN t_drivers_mileage.dispatcher_id   IS 'The ID of the Dispatcher that is guiding the Driver.';
COMMENT ON COLUMN t_drivers_mileage.company_id      IS 'The ID of the Company the Driver works for.';
COMMENT ON COLUMN t_drivers_mileage.driver_id       IS 'The ID of the Driver.';
COMMENT ON COLUMN t_drivers_mileage.mileage_data    IS 'The data that describes the Driver Mileage.';

-- changeset libra:017
-- comment: Add start_date and end_date column to t_drivers_mileage table
ALTER TABLE t_drivers_mileage
    ADD COLUMN start_date DATE,
    ADD COLUMN end_date   DATE;

COMMENT ON COLUMN t_drivers_mileage.start_date IS 'The start date of a particular mileage.';
COMMENT ON COLUMN t_drivers_mileage.end_date   IS 'The end date of a particular mileage.';

-- changeset libra:018
-- comment: Populate the start_date and end_date columns from t_drivers_mileage table with the corresponding data from mileage_data column
UPDATE t_drivers_mileage tdm
SET start_date = temp.min_date,
    end_date   = temp.max_date
FROM (
    SELECT
        id,
        MIN(to_date(temp_key, 'DD-MM-YYYY')) AS min_date,
        MAX(to_date(temp_key, 'DD-MM-YYYY')) AS max_date
    FROM t_drivers_mileage
    CROSS JOIN LATERAL jsonb_object_keys(mileage_data) AS temp_key
    GROUP BY id
) temp
WHERE tdm.id = temp.id;

-- changeset libra:019
-- comment: Drop the null constraint on start_date and end_date columns from t_drivers_mileage table
ALTER TABLE t_drivers_mileage
    ALTER COLUMN start_date SET NOT NULL,
    ALTER COLUMN end_date   SET NOT NULL;

-- changeset libra:020
-- comment: Create the t_trailers table and its related dependencies
CREATE SEQUENCE t_trailers_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_trailers (
    id              BIGINT PRIMARY KEY DEFAULT nextval('t_trailers_sequence'),
    uuid            uuid   NOT NULL UNIQUE,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    trailer_number  VARCHAR NOT NULL,
    vin_number      VARCHAR NOT NULL UNIQUE,
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
COMMENT ON COLUMN t_trailers.company_id      IS 'The identifier of the Company this trailer was created for.';

-- changeset libra:021
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
COMMENT ON COLUMN t_trucks.company_id      IS 'The identifier of the Company this truck was created for.';

-- changeset libra:022
-- comment: Remove truck_number, trailer_number, max_legal_weight_capacity, trailer_type and trailer_length columns from t_drivers table.
ALTER TABLE t_drivers
    DROP COLUMN truck_number,
    DROP COLUMN trailer_number,
    DROP COLUMN max_legal_weight_capacity,
    DROP COLUMN trailer_type,
    DROP COLUMN trailer_length;

-- changeset libra:023
-- comment: Add truck_id (t_trucks table) and trailer_id (t_trailers table) as foreign keys to t_drivers table.
ALTER TABLE t_drivers
    ADD COLUMN truck_id   BIGINT,
    ADD COLUMN trailer_id BIGINT,

    ADD CONSTRAINT fk_driver_truck   FOREIGN KEY (truck_id)   REFERENCES t_trucks(id),
    ADD CONSTRAINT fk_driver_trailer FOREIGN KEY (trailer_id) REFERENCES t_trailers(id);

COMMENT ON COLUMN t_drivers.truck_id   IS 'The identifier of the Truck that is currently assigned to this Driver.';
COMMENT ON COLUMN t_drivers.trailer_id IS 'The identifier of the Trailer that is currently assigned to this Driver.';

-- changeset libra:024
-- comment: Remove trailer_height column from t_drivers table.
ALTER TABLE t_drivers
    DROP COLUMN trailer_height;

-- changeset libra:025
-- comment: Create t_accounts table and its related dependencies.
CREATE SEQUENCE t_accounts_sequence
    INCREMENT BY 1
    START WITH 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE t_accounts (
    id              BIGINT  PRIMARY KEY DEFAULT nextval('t_accounts_sequence'),
    uuid            uuid    NOT NULL UNIQUE,
    username        VARCHAR NOT NULL,
    hashed_password VARCHAR NOT NULL,
    entity_type     VARCHAR NOT NULL,
    role            VARCHAR NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    entity_id       BIGINT  NOT NULL
);

ALTER SEQUENCE t_accounts_sequence OWNED BY t_accounts.id;

COMMENT ON TABLE t_accounts IS 'The table used to store the account data of various actors of the system.';

COMMENT ON COLUMN t_accounts.id              IS 'The primary key of the t_accounts table.';
COMMENT ON COLUMN t_accounts.uuid            IS 'The UUID of the Account.';
COMMENT ON COLUMN t_accounts.username        IS 'The associated username of the Account.';
COMMENT ON COLUMN t_accounts.hashed_password IS 'The hashed version of the password of the Account.';
COMMENT ON COLUMN t_accounts.entity_type     IS 'The type of the entity this Account pertains to.';
COMMENT ON COLUMN t_accounts.role            IS 'The representing role of the user of this Account.';
COMMENT ON COLUMN t_accounts.is_active       IS 'A boolean indicator specifying whether the account is active.';
COMMENT ON COLUMN t_accounts.entity_id       IS 'The identifier of entity that is represented by this account.';
