CREATE TABLE IF NOT EXISTS employments
(
    id bigint NOT NULL AUTO_INCREMENT,
    employment_status varchar,
    employer varchar,
    salary decimal,
    "position" varchar,
    work_experience_total integer,
    work_experience_current integer,
    CONSTRAINT employments_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS passports
(
    id bigint NOT NULL AUTO_INCREMENT,
    series varchar,
    "number" varchar,
    issue_date date,
    issue_branch varchar,
    CONSTRAINT passports_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_offers
(
    application_id bigint NOT NULL,
    requested_amount decimal,
    total_amount decimal,
    term integer,
    monthly_payment decimal,
    rate decimal,
    is_insurance_enabled boolean,
    is_salary_client boolean,
    CONSTRAINT loan_offers_pkey PRIMARY KEY (application_id)
);

CREATE TABLE IF NOT EXISTS clients
(
    id bigint NOT NULL AUTO_INCREMENT,
    last_name varchar,
    first_name varchar,
    middle_name varchar,
    email varchar,
    gender varchar,
    marital_status varchar,
    dependent_amount integer,
    passport_id bigint,
    employment_id bigint,
    account varchar,
    birth_date date,
    CONSTRAINT clients_pkey PRIMARY KEY (id),
    CONSTRAINT employment_id FOREIGN KEY (employment_id)
        REFERENCES employments (id),
    CONSTRAINT passport_id FOREIGN KEY (passport_id)
        REFERENCES passports (id)
);

CREATE TABLE IF NOT EXISTS credits
(
    id bigint NOT NULL AUTO_INCREMENT,
    amount decimal,
    term integer,
    monthly_payment decimal,
    rate decimal,
    psk decimal,
    is_insurance_enabled boolean,
    is_salary_client boolean,
    credit_status varchar,
    CONSTRAINT credits_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS applications
(
    id bigint NOT NULL AUTO_INCREMENT,
    status varchar,
    creation_date date,
    applied_offer_id bigint,
    sign_date date,
    ses_code varchar,
    client_id bigint,
    credit_id bigint,
    CONSTRAINT applications_id_pkey PRIMARY KEY (id),
    CONSTRAINT applied_offer_id_fkey FOREIGN KEY (applied_offer_id)
        REFERENCES loan_offers (application_id),
    CONSTRAINT client_id_fkey FOREIGN KEY (client_id)
        REFERENCES clients (id),
    CONSTRAINT credit_id_fkey FOREIGN KEY (credit_id)
        REFERENCES credits (id)
);

CREATE TABLE IF NOT EXISTS applications_status_history
(
    id bigint NOT NULL AUTO_INCREMENT,
    application_id bigint NOT NULL,
    status varchar,
    "time" timestamp without time zone,
    change_type varchar,
    CONSTRAINT applications_status_history_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS payments
(
    id bigint NOT NULL AUTO_INCREMENT,
    "number" integer,
    date date,
    total_payment decimal,
    interest_payment decimal,
    debt_payment decimal,
    remaining_debt decimal,
    credit_id bigint,
    CONSTRAINT payments_id_pkey PRIMARY KEY (id),
    CONSTRAINT credit_id_fkey_2 FOREIGN KEY (credit_id)
        REFERENCES credits (id)
);