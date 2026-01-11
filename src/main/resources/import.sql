--INSERT INTO tb_user (name, email, password) VALUES ('Lucas', 'lucas@gmail.com', '$2a$10$dM7v3Y9AUncErayHFkeR7Oo3Tb4ICzYVxSP5m/QlKOZ/AAyAUlVzm');
--INSERT INTO tb_user (name, email, password) VALUES ('Maria', 'maria@example.com', '$2a$10$dM7v3Y9AUncErayHFkeR7Oo3Tb4ICzYVxSP5m/QlKOZ/AAyAUlVzm');
--
INSERT INTO tb_user (name, email, password) VALUES ('Admin', 'admin@gmail.com', '$2a$10$IdLFSuaa6xb2cNM801mmLeuF7aXKULKm4EiETYizZBk9uwAKGVOhi');
INSERT INTO tb_role (authority) VALUES ('ROLE_USER');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_user_role(user_id, role_id) values (1, 1)
--
--INSERT INTO tb_user_role(user_id, role_id) values (2, 1)
--INSERT INTO tb_user_role(user_id, role_id) values (3, 1)
--INSERT INTO tb_user_role(user_id, role_id) values (3, 2)
--
--INSERT INTO tb_badges (name, icon_url) values ('Aprendiz condicional!', 'badge1.png');
--INSERT INTO tb_badges (name, icon_url) values ('Mestre do If/Else!', 'badge2.png');
--INSERT INTO tb_badges (name, icon_url) values ('Ninja Condicional!', 'badge3.png');
--INSERT INTO tb_badges (name, icon_url) values ('Vencedor de Torneio!', 'badge4.png');