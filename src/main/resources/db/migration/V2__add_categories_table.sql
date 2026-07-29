CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE posts ADD COLUMN category_id INTEGER;

ALTER TABLE posts 
ADD CONSTRAINT fk_posts_category 
FOREIGN KEY (category_id) 
REFERENCES categories(id);