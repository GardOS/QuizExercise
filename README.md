[![Build Status](https://travis-ci.org/GardOS/QuizExercise.svg?branch=master)](https://travis-ci.org/GardOS/QuizExercise)

## QuizExercise
This repository is for preparing for the home exam in *PG6100 Enterpriseprogramming 2*. 
Where the focus is to learn the tools, concepts and patterns that might be applied in a enterprise application. 

"Curriculum" and code inspiration can be found 
[here.](https://github.com/arcuri82/testing_security_development_enterprise_systems)

## Application
* **Codebase**: Kotlin 
* **Architecture**: Microservices
* **Wiring/Plumbing:** Spring-boot, Maven, Docker
* **Communication between services:** Eureka, Zuul, RabbitMQ, Hystrix, Ribbon
* **Testing:**  WireMock, RestAssured, TestContainer
* **Data:** Redis, H2, Postgres, JDBC
* **Other:** Travis, Swagger

## Running application
1. `mvn clean install -DskipTests`
2. `docker-compose build`
3. `docker-compose up -d`
4. When done: `docker-compose down` 

Run tests:
`mvn clean verify`

## About
The application is a single player quiz game. 

The user can create quizzes, questions and categories in the Quiz module using CRUD-operations. 
From there the user can play the quizzes from the game module. Once the user has finished a game, the score will be 
posted to the scoreboard in the Score module.

The rest of the components is to ensure proper communication between services in a secure manner.

![Diagram](./QuizExercise.png)  