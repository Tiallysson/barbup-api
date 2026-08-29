CREATE TABLE users (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    phone varchar(14),
    password varchar(255) NOT NULL,
    role smallint NOT NULL DEFAULT 0,
    email_verified boolean NOT NULL DEFAULT false,
    verification_code varchar(6),
    verification_code_expires_at timestamp,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE address (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    street varchar(255) NOT NULL,
    number varchar(255) NOT NULL,
    complement varchar(255),
    neighborhood varchar(255) NOT NULL,
    city varchar(255) NOT NULL,
    state varchar(2) NOT NULL,
    zipcode varchar(255) NOT NULL,
    latitude double precision,
    longitude double precision,
    CONSTRAINT pk_address PRIMARY KEY (id)
);

CREATE TABLE barbershop (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    name varchar(255) NOT NULL,
    slug varchar(255) NOT NULL,
    document varchar(255) NOT NULL,
    phone varchar(255),
    logo_url varchar(255) NOT NULL,
    address_id uuid NOT NULL,
    owner_id uuid NOT NULL,
    CONSTRAINT pk_barbershop PRIMARY KEY (id),
    CONSTRAINT uk_barbershop_address_id UNIQUE (address_id),
    CONSTRAINT fk_barbershop_address FOREIGN KEY (address_id) REFERENCES address (id),
    CONSTRAINT fk_barbershop_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE INDEX idx_barbershop_owner_id ON barbershop (owner_id);

CREATE TABLE barbershop_member (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    barbershop_id uuid NOT NULL,
    user_id uuid NOT NULL,
    role varchar(255),
    CONSTRAINT pk_barbershop_member PRIMARY KEY (id),
    CONSTRAINT fk_barbershop_member_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershop (id),
    CONSTRAINT fk_barbershop_member_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_barbershop_member_barbershop_user UNIQUE (barbershop_id, user_id)
);

CREATE INDEX idx_barbershop_member_user_id ON barbershop_member (user_id);

CREATE TABLE service (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    barbershop_id uuid NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    price numeric(38,2) NOT NULL,
    duration_minutes integer NOT NULL,
    CONSTRAINT pk_service PRIMARY KEY (id),
    CONSTRAINT fk_service_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershop (id)
);

CREATE INDEX idx_service_barbershop_id ON service (barbershop_id);

CREATE TABLE business_hours (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    barbershop_id uuid NOT NULL,
    day_of_week varchar(255) NOT NULL,
    open_time time NOT NULL,
    close_time time NOT NULL,
    CONSTRAINT pk_business_hours PRIMARY KEY (id),
    CONSTRAINT fk_business_hours_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershop (id),
    CONSTRAINT uk_business_hours_barbershop_day UNIQUE (barbershop_id, day_of_week)
);

CREATE TABLE barber_schedule (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    barbershop_member_id uuid NOT NULL,
    day_of_week varchar(255) NOT NULL,
    start_time time NOT NULL,
    end_time time NOT NULL,
    CONSTRAINT pk_barber_schedule PRIMARY KEY (id),
    CONSTRAINT fk_barber_schedule_member FOREIGN KEY (barbershop_member_id) REFERENCES barbershop_member (id)
);

CREATE INDEX idx_barber_schedule_member_id ON barber_schedule (barbershop_member_id);

CREATE TABLE barber_time_off (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    barbershop_member_id uuid NOT NULL,
    start_at timestamp NOT NULL,
    end_at timestamp NOT NULL,
    reason varchar(255) NOT NULL,
    CONSTRAINT pk_barber_time_off PRIMARY KEY (id),
    CONSTRAINT fk_barber_time_off_member FOREIGN KEY (barbershop_member_id) REFERENCES barbershop_member (id)
);

CREATE INDEX idx_barber_time_off_member_id ON barber_time_off (barbershop_member_id);

CREATE TABLE appointment (
    id uuid NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    created_by varchar(255),
    updated_by varchar(255),
    barbershop_id uuid NOT NULL,
    barbershop_member_id uuid NOT NULL,
    client_id uuid NOT NULL,
    service_id uuid NOT NULL,
    scheduled_at timestamptz NOT NULL,
    duration_minutes integer NOT NULL,
    price numeric(38,2) NOT NULL,
    status varchar(255) NOT NULL,
    notes varchar(255),
    created_by_source varchar(255) NOT NULL,
    CONSTRAINT pk_appointment PRIMARY KEY (id),
    CONSTRAINT fk_appointment_barbershop FOREIGN KEY (barbershop_id) REFERENCES barbershop (id),
    CONSTRAINT fk_appointment_member FOREIGN KEY (barbershop_member_id) REFERENCES barbershop_member (id),
    CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES users (id),
    CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES service (id)
);

CREATE INDEX idx_appointment_barbershop_id ON appointment (barbershop_id);
CREATE INDEX idx_appointment_member_id ON appointment (barbershop_member_id);
CREATE INDEX idx_appointment_client_id ON appointment (client_id);
CREATE INDEX idx_appointment_service_id ON appointment (service_id);
