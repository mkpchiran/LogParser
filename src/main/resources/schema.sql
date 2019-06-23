DROP TABLE IF EXISTS logcontainer.access_log;

CREATE TABLE logcontainer.access_log (
    id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    request_date DATETIME NOT NULL,
    ip VARCHAR(15) NOT NULL,
    request VARCHAR(20) NOT NULL,
    status INT(3) NOT NULL,
    user_agent VARCHAR(300) NOT NULL
);

CREATE TABLE IF NOT EXISTS logcontainer.access_log_result (
    id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ip VARCHAR(15) NOT NULL,
    comment VARCHAR(100) NOT NULL
);