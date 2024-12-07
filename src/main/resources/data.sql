-- Insert users
INSERT INTO uaa_users(id, username, `name`, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email) VALUES
                                                   (1,'user', 'mary li', '14765432111', '{bcrypt}$2a$10$yxeOZOvhc16NnAbIq3bHIe8Ja.rFPhAcDYhTEx0i1Nc.sIkWXfK6S', 1, 1, 1, 1, 'maryli@local.dev'),
                                                   (2,'kenny', 'kenken', '13455789101', '{SHA-1}{kHuRu6jV3+cBx9FDxGMln1bNI2y4DGo/BTXSxk1TD+o=}0dba8fb0fb9cf82a9e0c94f4063cf0def077b84d', 1, 1, 1, 1, 'kenny@local.dev');
insert into uaa_roles(id, role_name) values
                                          (1, 'ROLE_USER'),
                                          (2, 'ROLE_ADMIN');
-- Insert authorities
INSERT INTO uaa_users_roles(user_id, role_id) VALUES
                                                     (1, 1),
                                                     (1, 2),
                                                     (2, 1);