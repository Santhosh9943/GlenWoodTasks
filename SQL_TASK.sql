-- CREATE TABLE

postgres=# \c patientdb ;
You are now connected to database "patientdb" as user "postgres".
patientdb=# CREATE TABLE branch_details (branch_id SERIAL PRIMARY KEY,branch_name VARCHAR(50) NOT NULL,branch_location VARCHAR(50) NOT NULL);
CREATE TABLE
patientdb=# \d branch_details;
                                           Table "public.branch_details"
     Column      |         Type          | Collation | Nullable |                      Default                      
-----------------+-----------------------+-----------+----------+---------------------------------------------------
 branch_id       | integer               |           | not null | nextval('branch_details_branch_id_seq'::regclass)
 branch_name     | character varying(50) |           | not null | 
 branch_location | character varying(50) |           | not null | 
Indexes:
    "branch_details_pkey" PRIMARY KEY, btree (branch_id)

patientdb=# CREATE TABLE patient_details (patient_id SERIAL PRIMARY KEY,patient_name VARCHAR(50) NOT NULL,patient_age INTEGER NOT NULL,patient_dob DATE NOT NULL,patient_gender VARCHAR(10) NOT NULL,patient_mobile VARCHAR(20) UNIQUE NOT NULL,patient_email VARCHAR(50) NOT NULL,branch_id INTEGER REFERENCES branch_details(branch_id));
CREATE TABLE
patientdb=# \d patient_details;
                                           Table "public.patient_details"
     Column     |         Type          | Collation | Nullable |                       Default                       
----------------+-----------------------+-----------+----------+-----------------------------------------------------
 patient_id     | integer               |           | not null | nextval('patient_details_patient_id_seq'::regclass)
 patient_name   | character varying(50) |           | not null | 
 patient_age    | integer               |           | not null | 
 patient_dob    | date                  |           | not null | 
 patient_gender | character varying(10) |           | not null | 
 patient_mobile | character varying(20) |           | not null | 
 patient_email  | character varying(50) |           | not null | 
 branch_id      | integer               |           |          | 
Indexes:
    "patient_details_pkey" PRIMARY KEY, btree (patient_id)
    "patient_details_patient_mobile_key" UNIQUE CONSTRAINT, btree (patient_mobile)
Foreign-key constraints:
    "patient_details_branch_id_fkey" FOREIGN KEY (branch_id) REFERENCES branch_details(branch_id)

patientdb=# CREATE TABLE patient_history (id SERIAL PRIMARY KEY,patient_id INTEGER,patient_name TEXT,patient_age INTEGER,patient_dob DATE,patient_gender TEXT,patient_mobile TEXT,patient_email TEXT,branch_id INTEGER,insert_time TIMESTAMP);
CREATE TABLE
patientdb=# \d patient_history;
                                          Table "public.patient_history"
     Column     |            Type             | Collation | Nullable |                   Default                   
----------------+-----------------------------+-----------+----------+---------------------------------------------
 id             | integer                     |           | not null | nextval('patient_history_id_seq'::regclass)
 patient_id     | integer                     |           |          | 
 patient_name   | text                        |           |          | 
 patient_age    | integer                     |           |          | 
 patient_dob    | date                        |           |          | 
 patient_gender | text                        |           |          | 
 patient_mobile | text                        |           |          | 
 patient_email  | text                        |           |          | 
 branch_id      | integer                     |           |          | 
 insert_time    | timestamp without time zone |           |          | 
Indexes:
    "patient_history_pkey" PRIMARY KEY, btree (id)

-- CREATE INDEX
patientdb=# CREATE INDEX idx_patient_name ON patient_details (patient_name);
CREATE INDEX
patientdb=# CREATE INDEX idx_patient_email ON patient_details (patient_email);
CREATE INDEX
-- CREATE COMBINED INDEX
patientdb=# CREATE INDEX idx_patient_name_email ON patient_details (patient_name, patient_email);
CREATE INDEX
patientdb=# \di
                                List of relations
 Schema |                Name                | Type  |  Owner   |      Table      
--------+------------------------------------+-------+----------+-----------------
 public | branch_details_pkey                | index | postgres | branch_details
 public | idx_patient_email                  | index | postgres | patient_details
 public | idx_patient_name                   | index | postgres | patient_details
 public | idx_patient_name_email             | index | postgres | patient_details
 public | patient_details_patient_mobile_key | index | postgres | patient_details
 public | patient_details_pkey               | index | postgres | patient_details
 public | patient_history_pkey               | index | postgres | patient_history
(7 rows)

-- CREATE FUNCTION
postgres=# \c patientdb ;
You are now connected to database "patientdb" as user "postgres".
patientdb=# CREATE OR REPLACE FUNCTION insert_patient_history() 
RETURNS TRIGGER AS 
$$ 
BEGIN INSERT INTO patient_history (patient_id, patient_name, patient_age, patient_dob,patient_gender,patient_mobile,patient_email,branch_id,insert_time)
VALUES (NEW.patient_id, NEW.patient_name, NEW.patient_age, NEW.patient_dob,NEW.patient_gender, NEW.patient_mobile,NEW.patient_email,NEW.branch_id,NOW());
RETURN NEW;
END;
$$ 
LANGUAGE plpgsql;
CREATE FUNCTION
patientdb=# \df
                                 List of functions
 Schema |          Name          | Result data type | Argument data types |  Type   
--------+------------------------+------------------+---------------------+---------
 public | insert_patient_history | trigger          |                     | trigger
(1 row)

-- CREATE TRIGGER
patientdb=# CREATE TRIGGER after_insert_patient_details
AFTER INSERT ON patient_details
FOR EACH ROW
EXECUTE PROCEDURE insert_patient_history();
CREATE TRIGGER
patientdb=# \d patient_details;
                                           Table "public.patient_details"
     Column     |         Type          | Collation | Nullable |                       Default                       
----------------+-----------------------+-----------+----------+-----------------------------------------------------
 patient_id     | integer               |           | not null | nextval('patient_details_patient_id_seq'::regclass)
 patient_name   | character varying(50) |           | not null | 
 patient_age    | integer               |           | not null | 
 patient_dob    | date                  |           | not null | 
 patient_gender | character varying(10) |           | not null | 
 patient_mobile | character varying(20) |           | not null | 
 patient_email  | character varying(50) |           | not null | 
 branch_id      | integer               |           |          | 
Indexes:
    "patient_details_pkey" PRIMARY KEY, btree (patient_id)
    "patient_details_patient_mobile_key" UNIQUE CONSTRAINT, btree (patient_mobile)
    "idx_patient_email" btree (patient_email)
    "idx_patient_name" btree (patient_name)
    "idx_patient_name_email" btree (patient_name, patient_email)
Foreign-key constraints:
    "patient_details_branch_id_fkey" FOREIGN KEY (branch_id) REFERENCES branch_details(branch_id)
Triggers:
    after_insert_patient_details AFTER INSERT ON patient_details FOR EACH ROW EXECUTE PROCEDURE insert_patient_history()


-- INSERT DATA
patientdb=# INSERT INTO branch_details (branch_name,branch_location)VALUES('HP001','KK NAGAR'),('HP002','PORUR'),('HP003','ANNANAGAR'),('HP004','AVADI');
INSERT 0 4
patientdb=# SELECT * FROM branch_details;
 branch_id | branch_name | branch_location 
-----------+-------------+-----------------
         1 | HP001       | KK NAGAR
         2 | HP002       | PORUR
         3 | HP003       | ANNANAGAR
         4 | HP004       | AVADI
(4 rows)

patientdb=# INSERT INTO patient_details (patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email,branch_id) 
patientdb-# VALUES('Santhosh M',22,'2000-02-02','M','6380291755','msanthosh9943@gmail.com',1),('Varun KD',23,'1999-12-02','M','8585698578','varun@gmail.com',3),('Sharmila S',22,'2000-08-12','F','9685745869','sharmila@gmail.com',2),('Balaji K',23,'1999-02-28','M','9365287415','balaji@gmail.com',1),('Dhatchu D',23,'1999-03-18','M','7458236598','dhatchu@gmail.com',2),('Mani T',22,'2000-05-27','M','7451245784','mani@gmail.com',3),('Kani K',22,'1999-07-08','F','6859478598','kani@gmail.com',1);
INSERT 0 7
patientdb=# INSERT INTO patient_details (patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email) VALUES('Keerthana',21,'2001-02-08','F','8542698255','kani@gmail.com'),('Kani K',23,'2000-07-08','F','8579456987','ram@gmail.com'),('Balaji K',25,'1999-07-08','F','7548756987','mani@gmail.com');
INSERT 0 3
patientdb=# SELECT * FROM patient_details;
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |      patient_email      | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+-------------------------+-----------
          1 | Santhosh M   |          22 | 2000-02-02  | M              | 6380291755     | msanthosh9943@gmail.com |         1
          2 | Varun KD     |          23 | 1999-12-02  | M              | 8585698578     | varun@gmail.com         |         3
          3 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com      |         2
          4 | Balaji K     |          23 | 1999-02-28  | M              | 9365287415     | balaji@gmail.com        |         1
          5 | Dhatchu D    |          23 | 1999-03-18  | M              | 7458236598     | dhatchu@gmail.com       |         2
          6 | Mani T       |          22 | 2000-05-27  | M              | 7451245784     | mani@gmail.com          |         3
          7 | Kani K       |          22 | 1999-07-08  | F              | 6859478598     | kani@gmail.com          |         1
          9 | Keerthana    |          21 | 2001-02-08  | F              | 8542698255     | kani@gmail.com          |          
         10 | Kani K       |          23 | 2000-07-08  | F              | 8579456987      | ram@gmail.com           |          
         11 | Balaji K     |          25 | 1999-07-08  | F              | 7548756987     | mani@gmail.com          |          
(10 rows)

patientdb=# SELECT * FROM patient_history;
 id | patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |      patient_email      | branch_id |        insert_time         
----+------------+--------------+-------------+-------------+----------------+----------------+-------------------------+-----------+----------------------------
  1 |          1 | Santhosh M   |          22 | 2000-02-02  | M              | 6380291755     | msanthosh9943@gmail.com |         1 | 2023-03-04 10:19:41.387144
  2 |          2 | Varun KD     |          23 | 1999-12-02  | M              | 8585698578     | varun@gmail.com         |         3 | 2023-03-04 10:19:41.387144
  3 |          3 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com      |         2 | 2023-03-04 10:19:41.387144
  4 |          4 | Balaji K     |          23 | 1999-02-28  | M              | 9365287415     | balaji@gmail.com        |         1 | 2023-03-04 10:19:41.387144
  5 |          5 | Dhatchu D    |          23 | 1999-03-18  | M              | 7458236598     | dhatchu@gmail.com       |         2 | 2023-03-04 10:19:41.387144
  6 |          6 | Mani T       |          22 | 2000-05-27  | M              | 7451245784     | mani@gmail.com          |         3 | 2023-03-04 10:19:41.387144
  7 |          7 | Kani K       |          22 | 1999-07-08  | F              | 6859478598     | kani@gmail.com          |         1 | 2023-03-04 10:19:41.387144
  8 |          9 | Keerthana    |          21 | 2001-02-08  | F              | 8542698255     | kani@gmail.com          |           | 2023-03-04 10:23:15.990107
  9 |         10 | Kani K       |          23 | 2000-07-08  | F              | 8579456987      | ram@gmail.com           |           | 2023-03-04 10:23:15.990107
 10 |         11 | Balaji K     |          25 | 1999-07-08  | F              | 7548756987     | mani@gmail.com          |           | 2023-03-04 10:23:15.990107
(10 rows)



	
-- UPDATE DATA to specific/multiple column
-- specific column
patientdb=# UPDATE patient_details SET patient_age=24 WHERE patient_name='Balaji K';
UPDATE 2
--multiple column
patientdb=# UPDATE patient_details SET patient_age=22,patient_name='Manikandan T' WHERE patient_id=6;
UPDATE 1

-- INSERT table data into other table
patientdb=# CREATE TABLE branch_details_copy (branch_id INTEGER NOT NULL,branch_name VARCHAR(255),branch_location VARCHAR(255));
CREATE TABLE
patientdb=# INSERT INTO branch_details_copy (branch_id, branch_name, branch_location)
patientdb-# SELECT branch_id, branch_name, branch_location FROM branch_details;
INSERT 0 4

-- Create one new dummy table with same data present in existing table
patientdb=# CREATE TABLE patient_details_copy_with_data AS SELECT *FROM patient_details;
SELECT 10

-- Create one new table with same table structure without Data
patientdb=# CREATE TABLE patient_details_copy_without_data_like (LIKE patient_details INCLUDING ALL);
CREATE TABLE
   --OR
patientdb=# CREATE TABLE patient_details_copy_without_data_as AS TABLE patient_details WITH NO DATA;
CREATE TABLE AS

-- DROP/DELETE DATA
patientdb=# DELETE FROM patient_details_copy_with_data WHERE patient_id = 3;
DELETE 1
patientdb=# select * from patient_details_copy_with_data WHERE patient_id=3;
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile | patient_email | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+---------------+-----------
(0 rows)

patientdb=# TRUNCATE TABLE patient_details_copy_with_data;
TRUNCATE TABLE
patientdb=# SELECT * FROM patient_details_copy_with_data;
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile | patient_email | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+---------------+-----------
(0 rows)

patientdb=# DROP TABLE patient_details_copy_with_data;
DROP TABLE
patientdb=# \d patient_details_copy_with_data;
Did not find any relation named "patient_details_copy_with_data".

-- DELETE WITH CONDITIONS
DELETE FROM patient_details WHERE patient_name LIKE 'San%';
-- DELETE WITH DATE CONDITIONS
DELETE FROM patient_details WHERE patient_dob < '2000-01-01';

-- Creating sequence and updatupdatinging to table.
patientdb=# CREATE SEQUENCE patient_id_seq START WITH 1001;
CREATE SEQUENCE
patientdb=# \ds patient_id_seq ;
               List of relations
 Schema |      Name      |   Type   |  Owner   
--------+----------------+----------+----------
 public | patient_id_seq | sequence | postgres
(1 row)

patientdb=# ALTER TABLE patient_details ALTER COLUMN patient_id SET DEFAULT nextval('patient_id_seq');
ALTER TABLE
patientdb=# UPDATE patient_details SET patient_id = nextval('patient_id_seq');
UPDATE 10
patientdb=# SELECT * FROM patient_details;
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |      patient_email      | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+-------------------------+-----------
       1001 | Santhosh M   |          22 | 2000-02-02  | M              | 6380291755     | msanthosh9943@gmail.com |         1
       1002 | Varun KD     |          23 | 1999-12-02  | M              | 8585698578     | varun@gmail.com         |         3
       1003 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com      |         2
       1004 | Balaji K     |          23 | 1999-02-28  | M              | 9365287415     | balaji@gmail.com        |         1
       1005 | Dhatchu D    |          23 | 1999-03-18  | M              | 7458236598     | dhatchu@gmail.com       |         2
       1006 | Mani T       |          22 | 2000-05-27  | M              | 7451245784     | mani@gmail.com          |         3
       1007 | Kani K       |          22 | 1999-07-08  | F              | 6859478598     | kani@gmail.com          |         1
       1008 | Keerthana    |          21 | 2001-02-08  | F              | 8542698255     | kani@gmail.com          |          
       1009 | Kani K       |          23 | 2000-07-08  | F              | 8579456987     | ram@gmail.com           |          
       1010 | Balaji K     |          25 | 1999-07-08  | F              | 7548756987     | mani@gmail.com          |          
(10 rows)


-- split_part function in psql
patientdb=# SELECT
patient_name,
split_part(insert_time::text, '-', 1) AS insert_date,
split_part(insert_time::text, '-', 2) AS insert_month
FROM patient_history
LIMIT 5;
 patient_name | insert_date | insert_month 
--------------+-------------+--------------
 Santhosh M   | 2023        | 03
 Varun KD     | 2023        | 03
 Sharmila S   | 2023        | 03
 Balaji K     | 2023        | 03
 Dhatchu D    | 2023        | 03
(5 rows)

-- to_mm-dd-yyyy
patientdb=# SELECT to_char(insert_time, 'MM-DD-YYYY') AS formatted_date FROM patient_history LIMIT 5;
 formatted_date 
----------------
 03-04-2023
 03-04-2023
 03-04-2023
 03-04-2023
 03-04-2023
(5 rows)
-- to_date functions
patientdb=# SELECT to_date(insert_time::text, 'YYYY-MM-DD') AS converted_date FROM patient_history LIMIT 5;
 converted_date 
----------------
 2023-03-04
 2023-03-04
 2023-03-04
 2023-03-04
 2023-03-04
(5 rows)

--In operator Usage
patientdb=# SELECT * FROM patient_details WHERE patient_id IN (1001, 1003, 1005);
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |      patient_email      | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+-------------------------+-----------
       1001 | Santhosh M   |          22 | 2000-02-02  | M              | 6380291755     | msanthosh9943@gmail.com |         1
       1003 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com      |         2
       1005 | Dhatchu D    |          23 | 1999-03-18  | M              | 7458236598     | dhatchu@gmail.com       |         2
(3 rows)

  --(OR)In using Sub query
patientdb=# SELECT * FROM patient_details WHERE branch_id=(SELECT branch_id FROM branch_details WHERE branch_location='PORUR');
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |   patient_email    | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+--------------------+-----------
       1003 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com |         2
       1005 | Dhatchu D    |          23 | 1999-03-18  | M              | 7458236598     | dhatchu@gmail.com  |         2
(2 rows)


--GROUP BY
patientdb=# SELECT patient_gender, COUNT(*) FROM patient_details GROUP BY patient_gender;
 patient_gender | count 
----------------+-------
 M              |     5
 F              |     5
(2 rows)

--ORDER BY
patientdb=# SELECT * FROM patient_details ORDER BY patient_age ASC LIMIT 5;
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |      patient_email      | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+-------------------------+-----------
       1008 | Keerthana    |          21 | 2001-02-08  | F              | 8542698255     | kani@gmail.com          |          
       1003 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com      |         2
       1001 | Santhosh M   |          22 | 2000-02-02  | M              | 6380291755     | msanthosh9943@gmail.com |         1
       1006 | Mani T       |          22 | 2000-05-27  | M              | 7451245784     | mani@gmail.com          |         3
       1007 | Kani K       |          22 | 1999-07-08  | F              | 6859478598     | kani@gmail.com          |         1
(5 rows)


--Get duplicate Data from table with specified columns
patientdb=# SELECT patient_name,COUNT(*) as count FROM patient_details GROUP BY patient_name HAVING COUNT(*) > 1;
 patient_name | count 
--------------+-------
 Kani K       |     2
 Balaji K     |     2
(2 rows)


--Join Psql
  -- INNER JOIN
patientdb=# SELECT pd.patient_id,pd.patient_name,bd.branch_location
FROM patient_details AS pd
INNER JOIN branch_details AS bd
ON pd.branch_id = bd.branch_id;
 patient_id | patient_name | branch_location 
------------+--------------+-----------------
       1007 | Kani K       | KK NAGAR
       1004 | Balaji K     | KK NAGAR
       1001 | Santhosh M   | KK NAGAR
       1005 | Dhatchu D    | PORUR
       1003 | Sharmila S   | PORUR
       1006 | Mani T       | ANNANAGAR
       1002 | Varun KD     | ANNANAGAR
(7 rows)

  -- LEFT JOIN
patientdb=# SELECT pd.patient_id,pd.patient_name,bd.branch_location
FROM patient_details AS pd
LEFT JOIN branch_details AS bd
ON pd.branch_id = bd.branch_id;
 patient_id | patient_name | branch_location 
------------+--------------+-----------------
       1007 | Kani K       | KK NAGAR
       1004 | Balaji K     | KK NAGAR
       1001 | Santhosh M   | KK NAGAR
       1005 | Dhatchu D    | PORUR
       1003 | Sharmila S   | PORUR
       1006 | Mani T       | ANNANAGAR
       1002 | Varun KD     | ANNANAGAR
       1009 | Kani K       | 
       1010 | Balaji K     | 
       1008 | Keerthana    | 
(10 rows)

  -- RIGHT JOIN
patientdb=# SELECT pd.patient_id,pd.patient_name,bd.branch_location
FROM patient_details AS pd
RIGHT JOIN branch_details AS bd
ON pd.branch_id = bd.branch_id;
 patient_id | patient_name | branch_location 
------------+--------------+-----------------
       1007 | Kani K       | KK NAGAR
       1004 | Balaji K     | KK NAGAR
       1001 | Santhosh M   | KK NAGAR
       1005 | Dhatchu D    | PORUR
       1003 | Sharmila S   | PORUR
       1006 | Mani T       | ANNANAGAR
       1002 | Varun KD     | ANNANAGAR
            |              | AVADI
(8 rows)


-- update data for a column in a table by using another table 
patientdb=# UPDATE patient_details SET branch_id = (SELECT branch_id FROM branch_details where branch_name='HP002') FROM branch_details WHERE patient_id = 1002;
UPDATE 1

-- Inserting data into particular columns in a table by using columns of data from another table
patientdb=# INSERT INTO patient_history (patient_name,patient_dob,patient_gender,patient_mobile,branch_id) SELECT patient_name,patient_dob,patient_gender,patient_mobile,branch_id FROM patient_details where patient_id = 1003;
INSERT 0 1


-- Copy data TO CSV file with/without headers
  --WITH HEAD
COPY patient_details TO '/home/software/Desktop/GlenwoodsTask/CSV_With_Header.csv' WITH DELIMITER ',' CSV HEADER;
  --WITH OUT HEAD
COPY patient_details TO '/home/software/Desktop/GlenwoodsTask/CSV_With_out_Header.csv' WITH DELIMITER ',' CSV;

TRUNCATE TABLE PATIENT_details;

--Copy data FROM CSV file with/without headers

  --WITH HEAD
patientdb=# COPY patient_details (patient_id,patient_name,patient_age,patient_dob,patient_gender,patient_mobile,patient_email,branch_id) FROM '/home/software/Desktop/GlenwoodsTask/CSV_With_Header.csv' WITH DELIMITER ',' CSV HEADER;
COPY 10
    --(OR)TABLE COLUMNS ARE SAME IN CSV NO NEED TO DECLER EXPLISITELY
patientdb=# COPY patient_details_copy_without_data_like FROM '/home/software/Desktop/GlenwoodsTask/CSV_With_Header.csv' WITH DELIMITER ',' CSV HEADER;
COPY 10

  --WITH OUT HEAD
patientdb=# COPY patient_details_copy_without_data_as FROM '/home/software/Desktop/GlenwoodsTask/CSV_With_out_Header.csv' WITH DELIMITER ',' CSV;
COPY 10
patientdb=# SELECT * FROM patient_details_copy_without_data_as LIMIT 5;
 patient_id | patient_name | patient_age | patient_dob | patient_gender | patient_mobile |      patient_email      | branch_id 
------------+--------------+-------------+-------------+----------------+----------------+-------------------------+-----------
       1001 | Santhosh M   |          22 | 2000-02-02  | M              | 6380291755     | msanthosh9943@gmail.com |         1
       1003 | Sharmila S   |          22 | 2000-08-12  | F              | 9685745869     | sharmila@gmail.com      |         2
       1004 | Balaji K     |          23 | 1999-02-28  | M              | 9365287415     | balaji@gmail.com        |         1
       1005 | Dhatchu D    |          23 | 1999-03-18  | M              | 7458236598     | dhatchu@gmail.com       |         2
       1006 | Mani T       |          22 | 2000-05-27  | M              | 7451245784     | mani@gmail.com          |         3
(5 rows)


