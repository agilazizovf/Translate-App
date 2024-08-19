CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
);


CREATE TABLE phrasebooks (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             category_id BIGINT,
                             FOREIGN KEY (category_id) REFERENCES categories(id)
);


CREATE TABLE phrases (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         content TEXT NOT NULL,
                         phrase_book_id BIGINT,
                         FOREIGN KEY (phrase_book_id) REFERENCES phrasebooks(id)
);



-- Create users table
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE clients (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255),
                         surname VARCHAR(255),
                         username VARCHAR(255),
                         birthday DATE,
                         address VARCHAR(255),
                         phone VARCHAR(255),
                         email VARCHAR(255) UNIQUE,
                         user_id INT UNIQUE,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create authorities table
CREATE TABLE authorities (
    name VARCHAR(255) PRIMARY KEY
);

-- Create password_reset_tokens table
CREATE TABLE password_reset_tokens (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       token VARCHAR(255) UNIQUE NOT NULL,
                                       user_id INT,
                                       expiration_date TIMESTAMP NOT NULL,
                                       FOREIGN KEY (user_id) REFERENCES users(id)
);


-- Create translations table
CREATE TABLE translation (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             source_language VARCHAR(255) NOT NULL,
                             source_text TEXT NOT NULL,
                             target_language VARCHAR(255) NOT NULL,
                             target_text TEXT NOT NULL,
                             translated_at TIMESTAMP NOT NULL,
                             likes INT DEFAULT 0,
                             dislikes INT DEFAULT 0,
                             views INT DEFAULT 0,
                             user_id INT,
                             FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create comments table
CREATE TABLE comments (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          translation_id INT,
                          likes INT DEFAULT 0,
                          dislikes INT DEFAULT 0,
                          user_id INT,
                          FOREIGN KEY (translation_id) REFERENCES translation(id),
                          FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create user_authorities table
CREATE TABLE user_authorities (
                                  user_id INT,
                                  authority_name VARCHAR(255),
                                  FOREIGN KEY (user_id) REFERENCES users(id),
                                  FOREIGN KEY (authority_name) REFERENCES authorities(name),
                                  PRIMARY KEY (user_id, authority_name)
);

-- Create translation_likes table
CREATE TABLE translation_likes (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   translation_id INT,
                                   user_id INT,
                                   FOREIGN KEY (translation_id) REFERENCES translation(id),
                                   FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create translation_dislikes table
CREATE TABLE translation_dislikes (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      translation_id INT,
                                      user_id INT,
                                      FOREIGN KEY (translation_id) REFERENCES translation(id),
                                      FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create comment_likes table
CREATE TABLE comment_likes (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               comment_id INT,
                               user_id INT,
                               FOREIGN KEY (comment_id) REFERENCES comments(id),
                               FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create comment_dislikes table
CREATE TABLE comment_dislikes (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  comment_id INT,
                                  user_id INT,
                                  FOREIGN KEY (comment_id) REFERENCES comments(id),
                                  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create translation_views table
CREATE TABLE translation_views (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   translation_id INT,
                                   user_id INT,
                                   FOREIGN KEY (translation_id) REFERENCES translation(id),
                                   FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create user_favorites table
CREATE TABLE user_favorites (
                                user_id INT NOT NULL,
                                translation_id INT NOT NULL,
                                PRIMARY KEY (user_id, translation_id),
                                FOREIGN KEY (user_id) REFERENCES users(id),
                                FOREIGN KEY (translation_id) REFERENCES translation(id)
);

INSERT INTO categories (name) VALUES
                                  ('Essentials'),
                                  ('While Traveling'),
                                  ('Help / Medical'),
                                  ('At the Hotel'),
                                  ('At the Restaurant'),
                                  ('At the Bar'),
                                  ('At the Store'),
                                  ('At Work'),
                                  ('Time, Date, Numbers');

INSERT INTO phrasebooks (name, category_id) VALUES
                                                ('GREETING', 1),
                                                ('BASICS', 1),
                                                ('DIRECTIONS', 1),
                                                ('LANGUAGE', 1),
                                                ('MONEY', 1),
                                                ('GENERAL', 1);

INSERT INTO phrases (content, phrase_book_id) VALUES
                                                     ('Hello', 1),
                                                     ('My name is___', 1),
                                                     ('Excuse me', 1),
                                                     ('Goodbye', 1),
                                                     ('How are you', 1),
                                                     ('Nice to meet you', 1),
                                                     ('Please', 2),
                                                     ('I am sorry', 2),
                                                     ('Thank you', 2),
                                                     ('Left', 3),
                                                     ('Right', 3),
                                                     ('Straight ahead', 3),
                                                     ('In___ meters', 3),
                                                     ('Traffic light', 3),
                                                     ('Stop sign', 3),
                                                     ('North', 3),
                                                     ('South', 3),
                                                     ('East', 3),
                                                     ('West', 3),
                                                     ('Do you speak___', 4),
                                                     ('I do not speak___', 4),
                                                     ('I do not understand', 4),
                                                     ('I speak___', 4),
                                                     ('Where is the ATM', 5),
                                                     ('I want to exchange money', 5),
                                                     ('What is the exchange fee?', 5),
                                                     ('How much does it cost?', 5),
                                                     ('Where is the toilet', 6),
                                                     ('Where is the grocery store', 6),
                                                     ('This is an emergency', 6),
                                                     ('I need help', 6);


-- Insert into users
INSERT INTO users (username, password) VALUES
                                           ('admin', '$2a$12$ZCyOlICBONKb5Gv.FC69dOAIoPHQ8U/5KqNPIydYoH2zqinvhAlfW'),
                                           ('agil', '$2a$12$ZCyOlICBONKb5Gv.FC69dOAIoPHQ8U/5KqNPIydYoH2zqinvhAlfW'),
                                           ('elvin', '$2a$12$ZCyOlICBONKb5Gv.FC69dOAIoPHQ8U/5KqNPIydYoH2zqinvhAlfW');

-- Insert into authorities
INSERT INTO authorities (name) VALUES
                                   ('ADMIN'),
                                   ('USER');

-- Insert into password_reset_tokens
INSERT INTO password_reset_tokens (token, user_id, expiration_date)
VALUES ('sample-token-value', 2, '2024-08-18 12:34:56');

-- Insert into translation
INSERT INTO translation (source_language, source_text, target_language, target_text, translated_at, likes, dislikes, views, user_id) VALUES
    ('az', 'salam', 'en', 'hello', '2022-05-05 10:10:10', 1, 1, 1, 1);

-- Insert into comments
INSERT INTO comments (content, created_at, updated_at, translation_id, likes, dislikes, user_id) VALUES
    ('This is true', '2022-05-05 10:10:10', '2022-05-05 10:10:10', 1, 1, 1, 1);

-- Insert into user_authorities
INSERT INTO user_authorities (user_id, authority_name) VALUES
                                                           (1, 'USER'),
                                                           (2, 'USER'),
                                                           (3, 'ADMIN');

-- Insert into translation_likes
INSERT INTO translation_likes (translation_id, user_id) VALUES
    (1, 1);

-- Insert into clients
INSERT INTO clients (name, surname, username, birthday, address, phone, email, user_id)
VALUES ('Agil', 'Azizov', 'agil', '1985-06-15', '123 Elm Street', '+994555486811', 'aqilazizov2005@gmail.com', 2);

-- Insert into translation_dislikes
INSERT INTO translation_dislikes (translation_id, user_id) VALUES
    (1, 1);

-- Insert into comment_likes
INSERT INTO comment_likes (comment_id, user_id) VALUES
    (1, 1);

-- Insert into comment_dislikes
INSERT INTO comment_dislikes (comment_id, user_id) VALUES
    (1, 1);

-- Insert into translation_views
INSERT INTO translation_views (translation_id, user_id) VALUES
    (1, 1);

-- Insert into user_favorites
INSERT INTO user_favorites (user_id, translation_id) VALUES
    (1, 1);
