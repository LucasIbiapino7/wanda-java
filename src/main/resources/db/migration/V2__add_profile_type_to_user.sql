ALTER TABLE tb_user
    ADD COLUMN profile_type VARCHAR(20);

UPDATE tb_user
   SET profile_type = 'STUDENT'
 WHERE profile_type IS NULL;

ALTER TABLE tb_user
     ALTER COLUMN profile_type SET NOT NULL;

CREATE INDEX idx_user_profile_type ON tb_user(profile_type);