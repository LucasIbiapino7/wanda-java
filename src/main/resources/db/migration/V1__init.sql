-- =========================================================
-- V1__init.sql
-- Criação do schema inicial + seeds básicos
-- =========================================================

-- ========== Tabelas base ==========
CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tb_role (
    id BIGSERIAL PRIMARY KEY,
    authority VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tb_badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    icon_url VARCHAR(255)
);

CREATE TABLE tb_game (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE UNIQUE INDEX ux_tb_game_name ON tb_game(name);

-- ========== Join tables ==========
CREATE TABLE tb_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user  FOREIGN KEY (user_id) REFERENCES tb_user(id),
    CONSTRAINT fk_user_role_role  FOREIGN KEY (role_id) REFERENCES tb_role(id)
);

CREATE TABLE tb_user_badges (
    user_id BIGINT NOT NULL,
    badges_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, badges_id),
    CONSTRAINT fk_user_badges_user   FOREIGN KEY (user_id)   REFERENCES tb_user(id),
    CONSTRAINT fk_user_badges_badges FOREIGN KEY (badges_id) REFERENCES tb_badges(id)
);

CREATE TABLE tb_user_tournaments (
    user_id BIGINT NOT NULL,
    tournament_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, tournament_id)
);

-- ========== Demais entidades ==========
-- Player (@OneToOne @MapsId com User)
CREATE TABLE tb_player (
    id BIGINT PRIMARY KEY,
    number_of_matches INT,
    number_of_winners INT,
    nickname VARCHAR(255),
    character_url VARCHAR(255),
    wins_tournaments INT,
    CONSTRAINT fk_player_user FOREIGN KEY (id) REFERENCES tb_user(id)
);

CREATE TABLE tb_function (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    function TEXT,
    player_id BIGINT,
    game_id BIGINT,
    CONSTRAINT fk_function_player FOREIGN KEY (player_id) REFERENCES tb_user(id),
    CONSTRAINT fk_function_game   FOREIGN KEY (game_id)   REFERENCES tb_game(id)
);

-- Match (player1, player2, winner, game)
CREATE TABLE tb_match (
    id BIGSERIAL PRIMARY KEY,
    player1_id BIGINT,
    player2_id BIGINT,
    moment TIMESTAMP WITHOUT TIME ZONE,
    winner_id BIGINT,
    match_data TEXT,
    game_id BIGINT,
    CONSTRAINT fk_match_player1 FOREIGN KEY (player1_id) REFERENCES tb_user(id),
    CONSTRAINT fk_match_player2 FOREIGN KEY (player2_id) REFERENCES tb_user(id),
    CONSTRAINT fk_match_winner  FOREIGN KEY (winner_id)  REFERENCES tb_user(id),
    CONSTRAINT fk_match_game    FOREIGN KEY (game_id)    REFERENCES tb_game(id)
);

-- Challenge (challenger, challenged, status, createdAt, match (1-1), game)
CREATE TABLE tb_challenge (
    id BIGSERIAL PRIMARY KEY,
    challenger_id BIGINT,
    challenged_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    match_id BIGINT UNIQUE,
    game_id BIGINT,
    CONSTRAINT fk_challenge_challenger FOREIGN KEY (challenger_id) REFERENCES tb_user(id),
    CONSTRAINT fk_challenge_challenged FOREIGN KEY (challenged_id) REFERENCES tb_user(id),
    CONSTRAINT fk_challenge_match      FOREIGN KEY (match_id)      REFERENCES tb_match(id),
    CONSTRAINT fk_challenge_game       FOREIGN KEY (game_id)       REFERENCES tb_game(id)
);

CREATE TABLE tb_tournament (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    start_time TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(50),
    as_private BOOLEAN,
    password VARCHAR(255),
    max_participants INT,
    current_participants INT,
    bracket_json TEXT,
    creator_id BIGINT,
    winner_id BIGINT,
    game_id BIGINT,
    CONSTRAINT fk_tournament_game    FOREIGN KEY (game_id)  REFERENCES tb_game(id),
    CONSTRAINT fk_tournament_creator FOREIGN KEY (creator_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    CONSTRAINT fk_tournament_winner  FOREIGN KEY (winner_id)  REFERENCES tb_user(id) ON DELETE SET NULL
);

ALTER TABLE tb_user_tournaments
    ADD CONSTRAINT fk_user_tournaments_user       FOREIGN KEY (user_id)       REFERENCES tb_user(id),
    ADD CONSTRAINT fk_user_tournaments_tournament FOREIGN KEY (tournament_id) REFERENCES tb_tournament(id);

-- Logs dos agentes
CREATE TABLE tb_log_answers_agents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    assistant_style VARCHAR(50),
    moment TIMESTAMP WITHOUT TIME ZONE,
    code TEXT,
    answer TEXT,
    thought TEXT,
    valid BOOLEAN,
    feedback_user VARCHAR(255),
    function_name VARCHAR(255),
    CONSTRAINT fk_log_agents_user FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

-- ========== Índices auxiliares ==========
CREATE INDEX idx_function_player ON tb_function(player_id);
CREATE INDEX idx_function_game   ON tb_function(game_id);

CREATE INDEX idx_match_player1 ON tb_match(player1_id);
CREATE INDEX idx_match_player2 ON tb_match(player2_id);
CREATE INDEX idx_match_winner  ON tb_match(winner_id);
CREATE INDEX idx_match_game    ON tb_match(game_id);

CREATE INDEX idx_challenge_challenger ON tb_challenge(challenger_id);
CREATE INDEX idx_challenge_challenged ON tb_challenge(challenged_id);
CREATE INDEX idx_challenge_game       ON tb_challenge(game_id);

CREATE INDEX idx_tournament_game    ON tb_tournament(game_id);
CREATE INDEX idx_tournament_creator ON tb_tournament(creator_id);
CREATE INDEX idx_tournament_winner  ON tb_tournament(winner_id);

CREATE INDEX idx_log_agents_user ON tb_log_answers_agents(user_id);

-- =========================================================
-- SEED INICIAL
-- =========================================================

-- Usuário Admin
INSERT INTO tb_user (name, email, password)
VALUES ('Admin', 'admin@gmail.com', '$2a$10$IdLFSuaa6xb2cNM801mmLeuF7aXKULKm4EiETYizZBk9uwAKGVOhi');

-- Roles
INSERT INTO tb_role (authority) VALUES ('ROLE_USER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

-- Vínculo Admin -> ROLE_USER
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);

-- Player vinculado ao Admin (@MapsId => id = user_id)
INSERT INTO tb_player (id, number_of_matches, number_of_winners, nickname, character_url, wins_tournaments)
VALUES (1, 0, 0, 'Admin', 'p1.png', 0);

-- Game inicial: jokenpo
INSERT INTO tb_game (name, description)
VALUES ('jokenpo', 'Jogo clássico de pedra, papel e tesoura usado como base para desafios e torneios.');

-- Badges Iniciais
INSERT INTO tb_badges (name, icon_url) values ('Aprendiz condicional!', 'badge1.png');
INSERT INTO tb_badges (name, icon_url) values ('Mestre do If/Else!', 'badge2.png');
INSERT INTO tb_badges (name, icon_url) values ('Ninja Condicional!', 'badge3.png');
INSERT INTO tb_badges (name, icon_url) values ('Vencedor de Torneio!', 'badge4.png');