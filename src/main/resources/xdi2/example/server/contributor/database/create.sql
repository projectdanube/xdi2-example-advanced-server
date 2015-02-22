CREATE DATABASE books;
USE books;
DROP TABLE IF EXISTS books;
CREATE TABLE books (
     id                 int auto_increment,
     title              varchar(80),
     author             varchar(80),
     publisher          varchar(80),
     country            varchar(40),
     year               int,
     price              float,
     primary key(id)
);
GRANT ALL ON books.* to "books" identified by "books";
