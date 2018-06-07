package no.gardos.game

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.netflix.config.ConfigurationManager
import org.springframework.amqp.core.FanoutExchange
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.client.RestTemplate
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class GameApplicationConfig {

	init {
		val conf = ConfigurationManager.getConfigInstance()
		conf.setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", 5000)
	}

	@Bean
	fun swaggerApi(): Docket {
		return Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.paths(PathSelectors.any())
				.apis(RequestHandlerSelectors.basePackage("no.gardos.game"))
				.build()
	}

	private fun apiInfo(): ApiInfo {
		return ApiInfoBuilder()
				.title("REST API for interacting with the game")
				.version("1.0")
				.build()
	}

	@Bean(name = ["OBJECT_MAPPER_BEAN"])
	fun jsonObjectMapper(): ObjectMapper {
		return Jackson2ObjectMapperBuilder.json()
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.modules(JavaTimeModule())
				.build()
	}

	//Although bad practice we use this bean for testing purposes
	//The reason for this is because of complications of mocking a @LoadBalanced bean
	//There are probably ways to disable ribbon, or inject your own test beans, but its outside scope for this course
	@Bean
	@Profile("test")
	fun restTemplate(): RestTemplate {
		return RestTemplate()
	}

	@Bean
	@LoadBalanced
	fun loadBalancedRestTemplate(): RestTemplate {
		return RestTemplate()
	}

	@Bean
	fun fanout(): FanoutExchange {
		return FanoutExchange("game-over")
	}
}