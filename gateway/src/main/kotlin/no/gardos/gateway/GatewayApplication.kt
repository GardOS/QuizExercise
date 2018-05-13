package no.gardos.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import springfox.documentation.swagger.web.UiConfiguration
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableZuulProxy
@EnableSwagger2
class GatewayApplication

//docker-compose down && mvn package -DskipTests && docker-compose build && docker-compose up -d
fun main(args: Array<String>) {
	SpringApplication.run(GatewayApplication::class.java, *args)
}

@Bean
internal fun uiConfig(): UiConfiguration {
	return UiConfiguration("validatorUrl", "full", "alpha", "schema",
			UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L)
}