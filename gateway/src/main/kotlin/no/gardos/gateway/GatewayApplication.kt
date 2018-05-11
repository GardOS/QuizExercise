package no.gardos.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@SpringBootApplication
@EnableZuulProxy
class GatewayApplication

//docker-compose down && mvn package -DskipTests && docker-compose build && docker-compose up -d
fun main(args: Array<String>) {
	SpringApplication.run(GatewayApplication::class.java, *args)
}