CREATE TABLE COMPANY (
  COMPANY_ID INT NOT NULL,
  COMPANY_NAME VARCHAR(100) NOT NULL,
  PRIMARY KEY(COMPANY_ID)
);

CREATE TABLE PROJECT (
  PROJECT_ID INT NOT NULL,
  PROJECT_NAME VARCHAR(100) NOT NULL,
  PRIMARY KEY(PROJECT_ID)
);

CREATE TABLE PERSON_MANAGER (
  PERSON_MANAGER_ID INT NOT NULL,
  PERSON_MANAGER_NAME VARCHAR(100) NOT NULL,
  WORK_FOR INT NOT NULL,
  PRIMARY KEY(PERSON_MANAGER_ID)
);

CREATE TABLE PERSON_ENGINEER (
  PERSON_ENGINEER_ID INT NOT NULL,
  PERSON_ENGINEER_NAME VARCHAR(100) NOT NULL,
  WORK_FOR INT NOT NULL,
  PRIMARY KEY(PERSON_ENGINEER_ID)
);

CREATE TABLE MEETING (
  MEETING_ID INT NOT NULL,
  MEETING_TITLE VARCHAR(100) NOT NULL,
  FOR_PROJECT INT NOT NULL,
  PRIMARY KEY(MEETING_ID)
);

CREATE TABLE TRAINING_COURSE (
  TRAINING_COURSE_ID INT NOT NULL,
  TRAINING_COURSE_NAME VARCHAR(100) NOT NULL,
  FOR_PROJECT INT NOT NULL,
  PRIMARY KEY(TRAINING_COURSE_ID)
);

CREATE TABLE COMPANY_HAS_PROJECT (
  NMID INT NOT NULL,
  COMPANY_ID INT NOT NULL,
  PROJECT_ID INT NOT NULL,
  PRIMARY KEY(NMID)
);

CREATE TABLE MEETING_HAS_PERSON_MANAGER (
  NMID INT NOT NULL,
  MEETING_ID INT NOT NULL,
  PERSON_MANAGER_ID INT NOT NULL,
  PRIMARY KEY(NMID)
);

CREATE TABLE TRAINING_HAS_PERSON_ENGINEER (
  NMID INT NOT NULL,
  TRAINING_COURSE_ID INT NOT NULL,
  PERSON_ENGINEER_ID INT NOT NULL,
  PRIMARY KEY(NMID)
);

ALTER TABLE MEETING ADD CONSTRAINT REF_04 FOREIGN KEY (FOR_PROJECT)
    REFERENCES PROJECT(PROJECT_ID)
	ON DELETE CASCADE;

ALTER TABLE PERSON_ENGINEER ADD CONSTRAINT REF_03 FOREIGN KEY (WORK_FOR)
    REFERENCES COMPANY(COMPANY_ID)
	ON DELETE CASCADE;

ALTER TABLE TRAINING_HAS_PERSON_ENGINEER ADD CONSTRAINT REF_07 FOREIGN KEY (TRAINING_COURSE_ID)
    REFERENCES TRAINING_COURSE(TRAINING_COURSE_ID)
	ON DELETE CASCADE;

ALTER TABLE TRAINING_HAS_PERSON_ENGINEER ADD CONSTRAINT REF_08 FOREIGN KEY (PERSON_ENGINEER_ID)
    REFERENCES PERSON_ENGINEER(PERSON_ENGINEER_ID)
	ON DELETE CASCADE;

ALTER TABLE MEETING_HAS_PERSON_MANAGER ADD CONSTRAINT REF_06 FOREIGN KEY (MEETING_ID)
    REFERENCES MEETING(MEETING_ID)
	ON DELETE CASCADE;

ALTER TABLE MEETING_HAS_PERSON_MANAGER ADD CONSTRAINT REF_09 FOREIGN KEY (PERSON_MANAGER_ID)
    REFERENCES PERSON_MANAGER(PERSON_MANAGER_ID)
	ON DELETE CASCADE;
	  
ALTER TABLE TRAINING_COURSE ADD CONSTRAINT REF_05 FOREIGN KEY (FOR_PROJECT)
    REFERENCES PROJECT(PROJECT_ID)
	ON DELETE CASCADE;

ALTER TABLE PERSON_MANAGER ADD CONSTRAINT REF_00 FOREIGN KEY (WORK_FOR)
    REFERENCES COMPANY(COMPANY_ID)
	ON DELETE CASCADE;

ALTER TABLE COMPANY_HAS_PROJECT ADD CONSTRAINT REF_01 FOREIGN KEY (COMPANY_ID)
    REFERENCES COMPANY(COMPANY_ID)
	ON DELETE CASCADE;

ALTER TABLE COMPANY_HAS_PROJECT ADD CONSTRAINT REF_02 FOREIGN KEY (PROJECT_ID)
    REFERENCES PROJECT(PROJECT_ID)
	ON DELETE CASCADE;

INSERT INTO COMPANY VALUES(1, 'COMPANY 1');
INSERT INTO COMPANY VALUES(2, 'COMPANY 2');
INSERT INTO COMPANY VALUES(3, 'COMPANY 3');

INSERT INTO PROJECT VALUES(1, 'PROJECT 1');
INSERT INTO PROJECT VALUES(2, 'PROJECT 2');
INSERT INTO PROJECT VALUES(3, 'PROJECT 3');

INSERT INTO PERSON_MANAGER VALUES(1, 'MANAGER 1', 1);
INSERT INTO PERSON_MANAGER VALUES(2, 'MANAGER 2', 2);
INSERT INTO PERSON_MANAGER VALUES(3, 'MANAGER 3', 3);

INSERT INTO PERSON_ENGINEER VALUES(1, 'ENGINEER 1', 1);
INSERT INTO PERSON_ENGINEER VALUES(2, 'ENGINEER 2', 2);
INSERT INTO PERSON_ENGINEER VALUES(3, 'ENGINEER 3', 3);

INSERT INTO MEETING VALUES(1, 'MEETING 1', 1);
INSERT INTO MEETING VALUES(2, 'MEETING 2', 2);
INSERT INTO MEETING VALUES(3, 'MEETING 3', 3);

INSERT INTO TRAINING_COURSE VALUES(1, 'TRAINING COURSE 1', 1);
INSERT INTO TRAINING_COURSE VALUES(2, 'TRAINING COURSE 2', 2);
INSERT INTO TRAINING_COURSE VALUES(3, 'TRAINING COURSE 3', 3);

INSERT INTO COMPANY_HAS_PROJECT VALUES(1, 1, 1);
INSERT INTO COMPANY_HAS_PROJECT VALUES(2, 2, 2);
INSERT INTO COMPANY_HAS_PROJECT VALUES(3, 3, 3);

INSERT INTO MEETING_HAS_PERSON_MANAGER VALUES(1, 1, 1);
INSERT INTO MEETING_HAS_PERSON_MANAGER VALUES(2, 2, 2);
INSERT INTO MEETING_HAS_PERSON_MANAGER VALUES(3, 3, 3);

INSERT INTO TRAINING_HAS_PERSON_ENGINEER VALUES(1, 1, 1);
INSERT INTO TRAINING_HAS_PERSON_ENGINEER VALUES(2, 2, 2);
INSERT INTO TRAINING_HAS_PERSON_ENGINEER VALUES(3, 3, 3);

SELECT * FROM COMPANY;
SELECT * FROM PROJECT;
SELECT * FROM PERSON_MANAGER;
SELECT * FROM PERSON_ENGINEER;
SELECT * FROM MEETING;
SELECT * FROM TRAINING_COURSE;
SELECT * FROM COMPANY_HAS_PROJECT;
SELECT * FROM MEETING_HAS_PERSON_MANAGER;
SELECT * FROM TRAINING_HAS_PERSON_ENGINEER;