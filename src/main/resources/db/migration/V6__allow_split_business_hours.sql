ALTER TABLE business_hours DROP CONSTRAINT uk_business_hours_barbershop_day;

CREATE INDEX idx_business_hours_barbershop_day ON business_hours (barbershop_id, day_of_week);
