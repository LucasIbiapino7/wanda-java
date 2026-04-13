CREATE TABLE tb_function_history (
    id BIGSERIAL PRIMARY KEY,
    function_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    code TEXT NOT NULL,
    submitted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version_number INT NOT NULL,
    CONSTRAINT fk_function_history_function FOREIGN KEY (function_id) REFERENCES tb_function(id),
    CONSTRAINT fk_function_history_player FOREIGN KEY (player_id) REFERENCES tb_user(id),
    CONSTRAINT fk_function_history_game FOREIGN KEY (game_id) REFERENCES tb_game(id)
);

CREATE INDEX idx_function_history_function ON tb_function_history(function_id);
CREATE INDEX idx_function_history_player   ON tb_function_history(player_id);