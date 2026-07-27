CREATE USER 'kfs_test'@'localhost' IDENTIFIED BY 'a-password-you-choose';
GRANT ALL PRIVILEGES ON KFS_TEST.* TO 'kfs_test'@'localhost';
FLUSH PRIVILEGES;

SELECT user, host FROM mysql.user;

SHOW GRANTS FOR 'kfs_test'@'localhost';
