package no.gardos.player

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class PlayerApplication

//docker-compose down && mvn package -DskipTests && docker-compose build && docker-compose up -d
//http://localhost:8081/swagger-ui.html
fun main(args: Array<String>) {
	SpringApplication.run(PlayerApplication::class.java, *args)
}