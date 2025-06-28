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
