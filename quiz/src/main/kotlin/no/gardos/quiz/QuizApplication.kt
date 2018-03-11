package no.gardos.quiz

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableSwagger2
class QuizApplication {
	@Bean
	fun swaggerApi(): Docket {
		return Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.paths(PathSelectors.any())
				.apis(RequestHandlerSelectors.basePackage("no.gardos.quiz"))
				.build()
	}

	private fun apiInfo(): ApiInfo {
		return ApiInfoBuilder()
				.title("API for REST")
				.description("Some description")
				.version("1.0")
				.build()
	}

	@Bean(name = ["OBJECT_MAPPER_BEAN"])
	fun jsonObjectMapper(): ObjectMapper {
		return Jackson2ObjectMapperBuilder.json()
				.serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values?
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.modules(JavaTimeModule())
				.build()
	}
}

//mvn spring-boot:run
//http://localhost:8080/quizrest/api/swagger-ui.html
fun main(args: Array<String>) {
	SpringApplication.run(QuizApplication::class.java, *args)
}