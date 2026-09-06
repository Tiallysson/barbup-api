ALTER TABLE barbershop ADD CONSTRAINT uk_barbershop_name UNIQUE (name);
ALTER TABLE barbershop ADD CONSTRAINT uk_barbershop_slug UNIQUE (slug);