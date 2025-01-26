INSERT INTO tb_user (name, email, password) VALUES ('Lucas', 'lucas@example.com', '$2a$10$dM7v3Y9AUncErayHFkeR7Oo3Tb4ICzYVxSP5m/QlKOZ/AAyAUlVzm');
INSERT INTO tb_user (name, email, password) VALUES ('Mateus', 'mateus@example.com', 'senha123');
INSERT INTO tb_user (name, email, password) VALUES ('Paulo', 'paulo@example.com', 'senha123');
INSERT INTO tb_user (name, email, password) VALUES ('Maria', 'maria@example.com', 'senha123');
INSERT INTO tb_user (name, email, password) VALUES ('Admin', 'admin@gmail.com', '$2a$10$dM7v3Y9AUncErayHFkeR7Oo3Tb4ICzYVxSP5m/QlKOZ/AAyAUlVzm');

INSERT INTO tb_role (authority) VALUES ('ROLE_USER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role(user_id, role_id) values (1, 1)
INSERT INTO tb_user_role(user_id, role_id) values (2, 1)
INSERT INTO tb_user_role(user_id, role_id) values (3, 1)
INSERT INTO tb_user_role(user_id, role_id) values (4, 1)
INSERT INTO tb_user_role(user_id, role_id) values (5, 1)
INSERT INTO tb_user_role(user_id, role_id) values (5, 2)

INSERT INTO tb_function(name, function, player_id) VALUES ('jokenpo', 'function3', 3)