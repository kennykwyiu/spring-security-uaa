-- Drop existing tables if they exist
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS authorities;

-- Create users table
CREATE TABLE uaa_users (
                       username VARCHAR(50) NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       enabled TINYINT NOT NULL DEFAULT 1,
                       name VARCHAR NULL ,
                       PRIMARY KEY (username)
) ENGINE = InnoDB;

-- Create authorities table
CREATE TABLE uaa_authorities (
                             username VARCHAR(50) NOT NULL,
                             authority VARCHAR(50) NOT NULL,
                             CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES uaa_users(username)
) ENGINE=InnoDB;

-- Create unique index
CREATE UNIQUE INDEX ix_auth_username
    ON uaa_authorities (username, authority);