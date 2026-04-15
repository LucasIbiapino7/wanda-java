CREATE TABLE tb_session_event (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_label VARCHAR(50) NOT NULL,
    moment TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_session_event_user FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

CREATE INDEX idx_session_event_user ON tb_session_event(user_id);
CREATE INDEX idx_session_event_moment ON tb_session_event(moment);
CREATE INDEX idx_session_event_label ON tb_session_event(event_label);