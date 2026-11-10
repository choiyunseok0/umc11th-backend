USE umc11th;

-- Run once if the column is absent. Existing members receive the migration time.
ALTER TABLE member
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
