package no.gardos.player

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class PlayerApplication

fun main(args: Array<String>) {
	SpringApplication.run(PlayerApplication::class.java, *args)
}