package no.gardos.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import springfox.documentation.swagger.web.UiConfiguration
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@ComponentScan
@EnableZuulProxy
@EnableSwagger2
class GatewayApplication {
	@Bean
	internal fun swaggerUiConfig(): UiConfiguration {
		return UiConfiguration("validatorUrl", "full", "alpha", "schema",
				UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L)
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}
}

//docker-compose down && mvn package -DskipTests && docker-compose build && docker-compose up -d
fun main(args: Array<String>) {
	SpringApplication.run(GatewayApplication::class.java, *args)
}