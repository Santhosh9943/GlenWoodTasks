-- CREATE TABLE

CREATE TABLE branch_details (branch_id SERIAL PRIMARY KEY,branch_name VARCHAR(50) NOT NULL,branch_location VARCHAR(50) NOT NULL);

CREATE TABLE patient_details (patient_id SERIAL PRIMARY KEY,patient_name VARCHAR(50) NOT NULL,patient_age INTEGER NOT NULL,patient_dob DATE NOT NULL,patient_gender VARCHAR(10) NOT NULL,patient_mobile VARCHAR(20) UNIQUE NOT NULL,patient_email VARCHAR(50) NOT NULL,branch_id INTEGER REFERENCES branch_details(branch_id));

CREATE TABLE patient_history (id SERIAL PRIMARY KEY,patient_id INTEGER,patient_name TEXT,patient_age INTEGER,patient_dob DATE,patient_gender TEXT,patient_mobile TEXT,patient_email TEXT,branch_id INTEGER,insert_time TIMESTAMP);


-- INSERT DATA
INSERT INTO branch_details (branch_name,branch_location)VALUES('HP001','KK NAGAR'),('HP002','PORUR'),('HP003','ANNANAGAR');
INSERT INTO patient_details (patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email,branch_id) 
VALUES('Santhosh M',22,'2000-02-02','M','6380291755','msanthosh9943@gmail.com',1),('Varun KD',23,'1999-12-02','M','8585698578','varun@gmail.com',3),('Sharmila S',22,'2000-08-12','F','9685745869','sharmila@gmail.com',2),('Balaji K',23,'1999-02-28','M','9365287415','balaji@gmail.com',1),('Dhatchu D',23,'1999-03-18','M','7458236598','dhatchu@gmail.com',2),('Mani T',22,'2000-05-27','M','7451245784','mani@gmail.com',3),('Kani K',22,'1999-07-08','F','6859478598','kani@gmail.com',1);


	
-- UPDATE DATA to specific/multiple column
-- specific column
UPDATE patient_details SET patient_age=24 WHERE patient_name='Balaji K';
--multiple column
UPDATE patient_details SET patient_age=22,patient_name='Manikandan T' WHERE patient_id=6;

-- DROP/DELETE DATA
DROP TABLE patient_details;
TRUNCATE TABLE patient_details;
DELETE FROM patient_details WHERE patient_id = 3;

-- DELETE WITH CONDITIONS
DELETE FROM patient_details WHERE patient_name LIKE 'San%';
-- DELETE WITH DATE CONDITIONS
DELETE FROM patient_details WHERE patient_dob < '2000-01-01';

-- CREATE INDEX
CREATE INDEX idx_patient_name ON patient_details (patient_name);
CREATE INDEX idx_patient_email ON patient_details (patient_email);

-- CREATE COMBINED INDEX
CREATE INDEX idx_patient_name_email ON patient_details (patient_name, patient_email);

-- CREATE FUNCTION
CREATE OR REPLACE FUNCTION insert_patient_history() 
RETURNS TRIGGER AS 
$$ 
BEGIN INSERT INTO patient_history (patient_id, patient_name, patient_age, patient_dob,patient_gender,patient_mobile,patient_email,branch_id,insert_time)
VALUES (NEW.patient_id, NEW.patient_name, NEW.patient_age, NEW.patient_dob,NEW.patient_gender, NEW.patient_mobile,NEW.patient_email,NEW.branch_id,NOW());
RETURN NEW;
END;
$$ 
LANGUAGE plpgsql;

-- CREATE TRIGGER
CREATE OR REPLACE TRIGGER after_insert_patient_details
BEFORE INSERT ON patient_details
FOR EACH ROW
EXECUTE FUNCTION insert_patient_history();
-- ALTER TRIGGER TO ONE TABLE AND EXECUTE
CREATE OR REPLACE TRIGGER after_insert_patient_details
AFTER INSERT ON patient_details 
FOR EACH ROW
EXECUTE FUNCTION insert_patient_history();

-- INSERT table data into other table
CREATE TABLE branch_details_copy (branch_id INTEGER NOT NULL,branch_name VARCHAR(255),branch_location VARCHAR(255));
INSERT INTO branch_details_copy (branch_id, branch_name, branch_location)
SELECT branch_id, branch_name, branch_location FROM branch_details;

-- Create one new dummy table with same data present in existing table
CREATE TABLE patient_details_copy_with_data AS SELECT *FROM patient_details;

-- Create one new table with same table structure without Data
CREATE TABLE patient_details_copy_without_data_like (LIKE patient_details INCLUDING ALL);
   --OR
CREATE TABLE patient_details_copy_without_data_as AS TABLE patient_details WITH NO DATA;

-- Creating sequence and updatupdatinging to table.
CREATE SEQUENCE patient_id_seq START WITH 1;
ALTER SEQUENCE patient_id_seq RESTART WITH 1001;
ALTER TABLE patient_details ALTER COLUMN patient_id SET DEFAULT nextval('patient_id_seq');
UPDATE patient_details SET patient_id = nextval('patient_id_seq');

-- split_part function in psql
SELECT
split_part(insert_timestamp::text, '-', 1) AS insert_date,
split_part(insert_timestamp::text, '-', 2) AS insert_month
FROM patient_history;

-- to_mm-dd-yyyy
SELECT to_char(insertion_time, 'MM-DD-YYYY') AS formatted_date FROM patient_history;
-- to_date functions
SELECT to_date(insertion_time::text, 'YYYY-MM-DD') AS converted_date FROM patient_history;

--In operator Usage
SELECT * FROM patient_detials WHERE patient_id IN (1001, 1003, 1005);
  --(OR)In using Sub query
SELECT * FROM patient_detials WHERE patient_id IN (SELECT branch_id FROM branch_details WHERE branch_location='chennai');

--GROUP BY
SELECT patient_gender, COUNT(*) FROM patient_detials GROUP BY patient_gender;
--ORDER BY
SELECT * FROM patient_detials ORDER BY patient_age ASC;

--Get duplicate Data from table with specified columns
SELECT patient_name COUNT(*) as count 
FROM patient_detials 
GROUP BY patient_name
HAVING COUNT(*) > 1;

--Join Psql
  -- INNER JOIN
SELECT pd.patient_id,pd.patient_name,bd.branch_location
FROM patient_details AS pd
INNER JOIN branch_details AS bd
ON pd.branch_id = bd.branch_id;

  -- LEFT JOIN
SELECT pd.patient_id,pd.patient_name,bd.branch_location
FROM patient_details AS pd
LEFT JOIN branch_details AS bd
ON pd.branch_id = bd.branch_id;

  -- RIGHT JOIN
SELECT pd.patient_id,pd.patient_name,bd.branch_location
FROM patient_details AS pd
RIGHT JOIN branch_details AS bd
ON pd.branch_id = bd.branch_id;

-- update data for a column in a table by using another table 
UPDATE patient_details
SET branch_id = (SELECT branch_id FROM branch_details where branch_name='HP002')
FROM branch_details
WHERE patient_id = 1002;

-- Inserting data into particular columns in a table by using columns of data from another table
  --note:First We delete one row,because of both table having the mobile mobile column is not null with unique
    DELETE FROM patient_details WHERE patient_id =1003;
INSERT INTO patient_details (patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email,branch_id)
SELECT patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email,branch_id
FROM patient_history
where patient_id=1003;

-- Copy data TO CSV file with/without headers
  --WITH HEAD
COPY patient_details TO '/home/software/Desktop/GlenwoodsTask/CSV_With_Header.csv' WITH DELIMITER ',' CSV HEADER;
  --WITH OUT HEAD
COPY patient_details TO '/home/software/Desktop/GlenwoodsTask/CSV_With_out_Header.csv' WITH DELIMITER ',' CSV;

TRUNCATE TABLE PATIENT_details;

--Copy data FROM CSV file with/without headers
  --WITH HEAD
COPY patient_details(patient_id,patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email,branch_id) FROM '/home/software/Desktop/GlenwoodsTask/CSV_With_Header.csv' WITH DELIMITER ',' CSV HEADER;
    --(OR)TABLE COLUMNS ARE SAME IN CSV NO NEED TO DECLER EXPLISITELY
COPY patient_details FROM '/home/software/Desktop/GlenwoodsTask/CSV_With_Header.csv' WITH DELIMITER ',' CSV HEADER;
  --WITH OUT HEAD
COPY patient_details FROM '/home/software/Desktop/GlenwoodsTask/CSV_With_out_Header.csv' WITH DELIMITER ',' CSV;

--Inserting csv data into table from sftp


