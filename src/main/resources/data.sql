-- Insert users
INSERT INTO users(username, password, enabled) VALUES
                                                   ('user', '{bcrypt}$2a$10$yxeOZOvhc16NnAbIq3bHIe8Ja.rFPhAcDYhTEx0i1Nc.sIkWXfK6S', 1),
                                                   ('kenny', '{SHA-1}{kHuRu6jV3+cBx9FDxGMln1bNI2y4DGo/BTXSxk1TD+o=}0dba8fb0fb9cf82a9e0c94f4063cf0def077b84d', 1);

-- Insert authorities
INSERT INTO authorities(username, authority) VALUES
                                                 ('kenny', 'ROLE_USER'),
                                                 ('user', 'ROLE_USER'),
                                                 ('user', 'ROLE_ADMIN');