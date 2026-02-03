Activity Tracker – Initial Project Setup Guide

Backend Spring Boot:->
This document provides a step-by-step guide to set up and run the Activity Tracker application locally.

Prerequisites:->

Java JDK 17+ , MySQL Workbench & MYSQL Server8+ ,Eclipse (IDE) ,Postman (for API testing),

commands to check version:->  java -version

The versions used during development of this project are:  
 ->openjdk version "21.0.7" 2025-04-15 LTS
 ->OpenJDK Runtime Environment Temurin-21.0.7+6 (build 21.0.7+6-LTS)
 ->OpenJDK 64-Bit Server VM Temurin-21.0.7+6 (build 21.0.7+6-LTS, mixed mode, sharing)
-----------------------------------------
After Installing All Software Follow These below Step to run server:
********
Step 0 – Import Java Project into Eclipse :->(Required before running the application)

Open Eclipse > Go to File → Import > Select Maven >  Select Existing Maven Project > Click Next > Browse and select the project root folder > Click Finish

Wait for Maven dependencies to download automatically and setup which may take 2-5 min

********
Step 0.1 – Database Configuration (MySQL Connection)

Before starting the server, update database credentials in:

file path :-> src/main/resources/application.properties

->spring.datasource.url=jdbc:mysql://localhost:3306/activity_tracker   (database server url/database_name)
->spring.datasource.username=YOUR_MYSQL_USERNAME  (default:->root)
->spring.datasource.password=YOUR_MYSQL_PASSWORD  (default:root)

Notes:
username and password should match MySQL Workbench login credentials by default they were root

*********
Step 1: Create Database

Run the following SQL command in MySQL Workbench (One Time Process):

CREATE DATABASE activity_tracker;

The application uses this database.
*******
Step 2: Start Backend Application :-> Open the Java project in Eclipse

:->Run the Spring Boot application on top left navbar green icon to run server OR ActivityTrackerApplication (open this file) right click and run As java application

All Tables will be created automatically on server start & two roles were created and one admin user were create automatically and will inject in table

->Default Admin Credentials 
 ->Email: admin@khushibaby.org
 ->Password: password123

* use this credentials to generate intial jwt token *
*********
Step 3: Create normal User to access user dashboard 

:Use the Register API in Postman to create a new user. Ensure the correct role ID of user role is provided in the request body.

:This API requires a JWT token, which must be obtained by logging in with admin credentials, and then used to access the Register API.

Notes:

:Password is BCrypt encrypted -> store in db instead of plain text

:Admin registration does not require a program ID mandatory validation done on backend and user requires programId to register new user

*********
Note:> Authentication & Authorization

:All APIs except Login are secured via JWT Token which returned in response after login

:Pass the token in request headers for access secured APIs:

:->Authorization: Bearer <JWT_TOKEN>


