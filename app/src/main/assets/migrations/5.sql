ALTER TABLE Users ADD COLUMN Type VARCHAR(20);
ALTER TABLE Users ADD COLUMN HeadCircumUnit TEXT;

ALTER TABLE UserReadings ADD COLUMN HeadCircum DOUBLE;
