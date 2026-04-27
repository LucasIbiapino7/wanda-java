-- =========================================================
-- V8__create_tb_classroom.sql
-- Criacao da tabela de turmas
-- =========================================================

CREATE TABLE tb_classroom (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    course VARCHAR(255),
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    access_code VARCHAR(6) NOT NULL,
    mural TEXT,
    institution VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    instructor_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    CONSTRAINT fk_classroom_instructor FOREIGN KEY (instructor_id) REFERENCES tb_user(id),
    CONSTRAINT fk_classroom_game FOREIGN KEY (game_id) REFERENCES tb_game(id)
);

-- Unicidade do access_code apenas entre turmas ativas
CREATE UNIQUE INDEX ux_classroom_access_code_active
    ON tb_classroom (access_code)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_classroom_instructor ON tb_classroom(instructor_id);
CREATE INDEX idx_classroom_status ON tb_classroom(status);
CREATE INDEX idx_classroom_game ON tb_classroom(game_id);