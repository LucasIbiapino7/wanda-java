-- =========================================================
-- V3__add_audit_timestamps.sql
-- Adiciona campos de auditoria em tb_user e tb_function
-- =========================================================

-- tb_user: data de cadastro do aluno
ALTER TABLE tb_user
    ADD COLUMN created_at TIMESTAMP WITHOUT TIME ZONE;

-- tb_function: data de primeira submissão e última atualização
ALTER TABLE tb_function
    ADD COLUMN created_at  TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE tb_function
    ADD COLUMN updated_at  TIMESTAMP WITHOUT TIME ZONE;

-- Índices para queries de auditoria por período
CREATE INDEX idx_user_created_at     ON tb_user(created_at);
CREATE INDEX idx_function_created_at ON tb_function(created_at);
CREATE INDEX idx_function_updated_at ON tb_function(updated_at);