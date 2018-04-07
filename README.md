[![Build Status](https://travis-ci.org/GardOS/QuizExercise.svg?branch=master)](https://travis-ci.org/GardOS/QuizExercise)

## QuizExercise
Exercise in subject *PG6100 Enterpriseprogramming 2*. "Curriculum" and code inspiration can be found 
[here.](https://github.com/arcuri82/testing_security_development_enterprise_systems)

## Application
* Codebase: Kotlin. 
* Architecture: Microservices

Tools, Frameworks etc.:
* Spring
* Maven
* Docker
* Travis
* Swagger

## Running application
In terminal:
1. Run `mvn clean install` (include -DskipTests to skip tests)  
2. Run `docker-compose build`  
3. Run `docker-compose up -d` 

From there on you can test endpoints using a tool such as Postman.

When done: `docker-compose down -d` 