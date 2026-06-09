-- =========================================================
-- V9__create_tb_classroom_student.sql
-- Tabela de juncao entre turmas e alunos
-- =========================================================

CREATE TABLE tb_classroom_student (
    classroom_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    joined_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    PRIMARY KEY (classroom_id, student_id),
    CONSTRAINT fk_cs_classroom FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id),
    CONSTRAINT fk_cs_student   FOREIGN KEY (student_id)   REFERENCES tb_user(id)
);

CREATE INDEX idx_cs_classroom ON tb_classroom_student(classroom_id);
CREATE INDEX idx_cs_student   ON tb_classroom_student(student_id);