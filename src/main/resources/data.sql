delete from uaa_roles_permissions;
delete from uaa_users_roles;
delete from uaa_permissions;
delete from uaa_users;
delete from uaa_roles;
insert into uaa_permissions(id, permission_name, display_name)
values (1, 'USER_READ', 'View User Information'),
       (2, 'USER_CREATE', 'Create User'),
       (3, 'USER_UPDATE', 'Edit User Information'),
       (4, 'USER_ADMIN', 'User Administration');

-- Insert users
INSERT INTO uaa_users(id, username, `name`, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, using_mfa, mfa_key, email) VALUES
                                                   (1,'user', 'mary li', '14765432111', '{bcrypt}$2a$10$yxeOZOvhc16NnAbIq3bHIe8Ja.rFPhAcDYhTEx0i1Nc.sIkWXfK6S', 1, 1, 1, 1, true, '8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==', 'maryli@local.dev'),
                                                   (2,'kenny', 'kenken', '13455789101', '{SHA-1}{kHuRu6jV3+cBx9FDxGMln1bNI2y4DGo/BTXSxk1TD+o=}0dba8fb0fb9cf82a9e0c94f4063cf0def077b84d', 1, 1, 1, 1, false, '8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==', 'kenny@local.dev');
insert into uaa_roles(id, role_name, display_name, built_in)
values (1, 'ROLE_USER', 'Client User', true),
       (2, 'ROLE_ADMIN', 'Super Administrator', true),
       (3, 'ROLE_STAFF', 'Admin Panel User', true);

insert into uaa_users_roles(user_id, role_id) values (1, 1), (1, 2), (1, 3), (2, 1);
insert into uaa_roles_permissions(role_id, permission_id) values (1, 1), (2, 1), (2, 2), (2, 3), (2, 4);