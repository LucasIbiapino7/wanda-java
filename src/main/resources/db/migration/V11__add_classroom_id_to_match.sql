ALTER TABLE tb_match
    ADD COLUMN classroom_id BIGINT,
    ADD CONSTRAINT fk_match_classroom
        FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id);

CREATE INDEX idx_match_classroom ON tb_match(classroom_id);