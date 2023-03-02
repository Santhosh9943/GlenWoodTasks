SHOW DATABASES;
-- DROP DATABASE Database_Name;
CREATE DATABASE IF NOT EXISTS practics;
USE practics;
-- TABLE CURD --
SHOW TABLES;
-- DROP TABLE Table_Name;

/* Creat Table*/
CREATE TABLE IF NOT EXISTS emp(
id INT NOT NULL AUTO_INCREMENT,
name VARCHAR(20) NOT NULL,
mob VARCHAR(10) NOT NULL,
PRIMARY KEY(id));

/* Describe Table*/
DESC emp;

/* Rename Table*/
ALTER TABLE emp RENAME TO emploee;

/* Add Column in Table*/
ALTER TABLE emp ADD email VARCHAR(50) NOT NULL; -- Or ALTER TABLE emp ADD COLUMN email VARCHAR(50) NOT NULL;

/* Rename Column in Table*/
ALTER TABLE emp RENAME COLUMN emaill TO email;

/* Alter Column Properitys in Table*/
ALTER TABLE emp MODIFY email INT(10) NOT NULL; -- Only Change The Column Property
ALTER TABLE emp CHANGE COLUMN email email2 INT(10) NOT NULL; -- Thish is Also Change The name With Property

/* Alter Primary key For Column in Table*/
ALTER TABLE emp DROP PRIMARY KEY,ADD PRIMARY KEY (id, emaill);

/* Drop Column in Table*/
ALTER TABLE emp DROP email; -- Or ALTER TABLE emp DROP COLUMN email;

-- CURD VALUES --

/* Insert Values*/
INSERT INTO emp (id,name,mob,email) values 
(1,'Santhosh M','6380291755','msanthosh9943@gmail.com'),
(2,'Dhatchu','85324563','sfrfgsxfg@gmail.com'),
(3,'balaji','447454455','tgjhnxdfgn@gmail.com'),
(4,'kani M','5457858555','xfgbxfgb@gmail.com'),
(5,'sharmi M','55845554','hgfgghhgb@gmail.com'),
(6,'keerthu M','5557856252','dfcb@gmail.com');
-- OR
INSERT INTO emp (id,name,mob,email) values (7,'ayesha M','48855545','zdxfgxhnff@gmail.com');
INSERT INTO emp (id,name,mob,email) values (8,'sadhu M','458547855','xfgbhsdftgadf@gmail.com');
-- OR
INSERT INTO emp (name,mob,email) values ('varun M','9857552585','zdfcbdfbv@gmail.com');

/* Show All Table Values*/
SELECT * FROM emp;

/* Delete Values*/
DELETE FROM emp WHERE id=8;

/* Update Values*/
UPDATE emp SET name='Varun D' WHERE id=8;
