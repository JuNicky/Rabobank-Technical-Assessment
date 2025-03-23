create table if not exists users (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_name varchar(255) NOT NULL
);

create table if not exists books (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    author varchar(255) NOT NULL,
    is_available BOOLEAN NOT NULL,
    borrower_id INT DEFAULT NULL,
    FOREIGN KEY (borrower_id) REFERENCES users(id) ON DELETE SET NULL
);