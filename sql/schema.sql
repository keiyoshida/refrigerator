--DROP TABLE refrigerator IF exists;

CREATE TABLE IF NOT exists refrigerator (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                           name char NOT NULL,
                           limitDay DATE NOT NULL);

CREATE TABLE IF NOT exists overlimitdata (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                           name char NOT NULL,
                           limitDay DATE NOT NULL);