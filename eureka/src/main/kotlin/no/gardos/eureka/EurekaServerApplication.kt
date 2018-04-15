package no.gardos.eureka

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaServerApplication

//http://localhost:8761
fun main(args: Array<String>) {
	SpringApplication.run(EurekaServerApplication::class.java, *args)
}