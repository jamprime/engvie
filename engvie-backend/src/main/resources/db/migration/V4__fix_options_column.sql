-- Change options column from JSONB to TEXT to fix Hibernate String → JSONB type error
ALTER TABLE game_rounds ALTER COLUMN options TYPE TEXT USING options::text;
