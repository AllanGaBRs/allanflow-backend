ALTER TABLE tb_user
ADD COLUMN google_subject VARCHAR(255);

ALTER TABLE tb_user
ADD CONSTRAINT uk_tb_user_google_subject
UNIQUE (google_subject);