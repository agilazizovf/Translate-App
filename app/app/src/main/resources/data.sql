INSERT INTO users (username, password)
VALUES ('agil', '{noop}1234'),
       ('elvin', '{noop}1234');

INSERT INTO authorities (name) VALUES ('ADMIN'), ('USER');

INSERT INTO password_reset_tokens (username, token, expiration_date)
VALUES ('agil', 'bfe8ncr84ur74y8xb48ry74yr8t4f7y47yf84yf8u437fd', '2024-05-05 10:10:10');

INSERT INTO translation (source_language, source_text, target_language, target_text, translated_at, user_id)
VALUES ('az', 'salam', 'en', 'hello', '2022-05-05 10:10:10', 1);

INSERT INTO comments (content, created_at, updated_at, translation_id, user_id)
VALUES ('This is true', '2022-05-05 10:10:10', '2022-05-05 10:10:10', 1, 1);

INSERT INTO user_authorities (user_id, authority_name)
VALUES (1, 'USER');

