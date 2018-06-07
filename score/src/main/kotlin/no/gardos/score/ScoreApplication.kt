package no.gardos.score

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class ScoreApplication

fun main(args: Array<String>) {
	SpringApplication.run(ScoreApplication::class.java, *args)
}