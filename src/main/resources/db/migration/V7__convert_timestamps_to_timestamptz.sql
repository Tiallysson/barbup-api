ALTER TABLE users
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN verification_code_expires_at TYPE timestamptz USING verification_code_expires_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE address
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE barbershop
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE barbershop_member
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE service
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE business_hours
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE barber_schedule
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE barber_time_off
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN start_at TYPE timestamptz USING start_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN end_at TYPE timestamptz USING end_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE appointment
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE password_reset_code
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN expires_at TYPE timestamptz USING expires_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN consumed_at TYPE timestamptz USING consumed_at AT TIME ZONE 'America/Sao_Paulo';

ALTER TABLE password_reset_tokens
    ALTER COLUMN created_at TYPE timestamptz USING created_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN updated_at TYPE timestamptz USING updated_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN expires_at TYPE timestamptz USING expires_at AT TIME ZONE 'America/Sao_Paulo',
    ALTER COLUMN used_at TYPE timestamptz USING used_at AT TIME ZONE 'America/Sao_Paulo';
