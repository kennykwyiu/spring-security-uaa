CREATE TABLE IF NOT EXISTS uaa_roles (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          role_name VARCHAR(50) NOT NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT uk_uaa_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS uaa_users (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          account_non_expired BIT NOT NULL,
                                          account_non_locked BIT NOT NULL,
                                          credentials_non_expired BIT NOT NULL,
                                          email VARCHAR(254) NOT NULL,
                                          enabled BIT NOT NULL,
                                          mobile VARCHAR(11) NOT NULL,
                                          name VARCHAR(50) NOT NULL,
                                          password_hash VARCHAR(80) NOT NULL,
                                          username VARCHAR(50) NOT NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT uk_uaa_users_username UNIQUE (username),
                                          CONSTRAINT uk_uaa_users_mobile UNIQUE (mobile),
                                          CONSTRAINT uk_uaa_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS uaa_users_roles (
                                                user_id BIGINT NOT NULL,
                                                role_id BIGINT NOT NULL,
                                                PRIMARY KEY (user_id, role_id),
                                                CONSTRAINT fk_users_roles_user_id_uaa_users_id FOREIGN KEY (user_id) REFERENCES uaa_users (id),
                                                CONSTRAINT fk_users_roles_role_id_uaa_roles_id FOREIGN KEY (role_id) REFERENCES uaa_roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into uaa_users(id, username, `name`, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email)
values (1,'user', 'mary li', '14765432111', '{bcrypt}$2a$10$yxeOZOvhc16NnAbIq3bHIe8Ja.rFPhAcDYhTEx0i1Nc.sIkWXfK6S', 1, 1, 1, 1, 'maryli@local.dev'),
       (2,'kenny', 'kenken', '13455789101', '{SHA-1}{kHuRu6jV3+cBx9FDxGMln1bNI2y4DGo/BTXSxk1TD+o=}0dba8fb0fb9cf82a9e0c94f4063cf0def077b84d', 1, 1, 1, 1, 'kenny@local.dev');
insert into uaa_roles(id, role_name) values (1, 'ROLE_USER'), (2, 'ROLE_ADMIN');
insert into uaa_users_roles(user_id, role_id) values (1, 1), (1, 2), (2, 1);