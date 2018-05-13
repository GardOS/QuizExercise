package no.gardos.quiz

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class QuizApplication

fun main(args: Array<String>) {
	SpringApplication.run(QuizApplication::class.java, *args)
}