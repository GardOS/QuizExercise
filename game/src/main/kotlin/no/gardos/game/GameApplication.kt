package no.gardos.game

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.ribbon.RibbonClient

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "quiz")
class GameApplication

fun main(args: Array<String>) {
	SpringApplication.run(GameApplication::class.java, *args)
}