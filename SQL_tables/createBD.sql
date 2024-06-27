CREATE TABLE documents (
  id SERIAL PRIMARY KEY,
  doc_name VARCHAR(255) NOT NULL,
  date_create date NOT NULL,
  date_added_to_db TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_deleted TIMESTAMP,
  category VARCHAR(255),
  description VARCHAR(255) NOT NULL,
  data_article_id int NOT NULL
)

CREATE TABLE constants (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL
)

insert into constants (name, value)
values 
('URL', 'https://fstec.ru/dokumenty/vse-dokumenty'),
('PATH', 'C:/dokumenty_fstec')