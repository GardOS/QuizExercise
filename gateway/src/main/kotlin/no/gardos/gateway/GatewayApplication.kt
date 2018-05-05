package no.gardos.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@EnableZuulProxy //Todo: Investigate spring cloud gateway
@SpringBootApplication
@EnableEurekaClient
class GatewayApplication

fun main(args: Array<String>) {
	SpringApplication.run(GatewayApplication::class.java, *args)
}