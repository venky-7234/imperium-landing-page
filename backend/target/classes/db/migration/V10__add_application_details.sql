ALTER TABLE applications
ADD COLUMN company VARCHAR(150),
ADD COLUMN industry VARCHAR(150),
ADD COLUMN annual_revenue VARCHAR(100),
ADD COLUMN years_in_business VARCHAR(50),
ADD COLUMN why_attend TEXT,
ADD COLUMN what_value TEXT,
ADD COLUMN referred_by VARCHAR(150);
