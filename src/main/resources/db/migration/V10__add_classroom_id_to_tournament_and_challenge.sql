-- =========================================================
-- V10__add_classroom_id_to_tournament_and_challenge.sql
-- Vincula torneios e desafios a uma turma (nullable)
-- null = global, preenchido = pertence à turma
-- =========================================================

ALTER TABLE tb_tournament
    ADD COLUMN classroom_id BIGINT,
    ADD CONSTRAINT fk_tournament_classroom
        FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id);

ALTER TABLE tb_challenge
    ADD COLUMN classroom_id BIGINT,
    ADD CONSTRAINT fk_challenge_classroom
        FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id);

CREATE INDEX idx_tournament_classroom ON tb_tournament(classroom_id);
CREATE INDEX idx_challenge_classroom  ON tb_challenge(classroom_id);