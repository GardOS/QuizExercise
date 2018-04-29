package no.gardos.quiz

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class QuizApplication

//docker-compose down && mvn package -DskipTests && docker-compose build && docker-compose up -d
//http://localhost:8080/swagger-ui.html
fun main(args: Array<String>) {
	SpringApplication.run(QuizApplication::class.java, *args)
}