ALTER TABLE service
    ADD CONSTRAINT ck_service_duration_minutes_positive CHECK (duration_minutes > 0);

ALTER TABLE appointment
    ADD CONSTRAINT ck_appointment_duration_minutes_positive CHECK (duration_minutes > 0);
