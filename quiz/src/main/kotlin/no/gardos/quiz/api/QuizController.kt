package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.entity.Quiz
import no.gardos.quiz.model.repository.QuizRepository
import no.gardos.schema.CategoryDto
import no.gardos.schema.QuizDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import javax.validation.ConstraintViolationException

@Api(value = "/quiz", description = "API for quizzes.")
@RequestMapping(
		path = ["/quiz"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class QuizController {
	@Autowired
	private lateinit var rest: RestTemplate

	@Autowired
	private lateinit var quizRepo: QuizRepository

	@ApiOperation("Create new quiz")
	@PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
	@ApiResponse(code = 201, message = "The id of newly created quiz")
	fun createQuiz(
			@ApiParam("Quiz name. Should not specify id")
			@RequestBody
			dto: QuizDto
	): ResponseEntity<Long> {
		//Id is auto-generated and should not be specified
		if (dto.id != null) {
			return ResponseEntity.status(400).build()
		}

		val response: ResponseEntity<CategoryDto> = try {
			val url = "http://question-server/categories/${dto.id}" //Todo: Don't hardcode url

			rest.getForEntity(url, CategoryDto::class.java)
		} catch (e: HttpClientErrorException) {
			return ResponseEntity.status(e.statusCode.value()).build()
		}

		val quiz = quizRepo.save(Quiz())

		return ResponseEntity.status(201).body(quiz.id)
	}

	@ApiOperation("Test Eureka load handling using config values from docker container")
	@GetMapping(path = ["/eureka"], produces = [(MediaType.TEXT_PLAIN_VALUE)])
	fun testEureka(): ResponseEntity<String> {
		val response = try {
			val url = "http://question-server/categories/eureka"
			rest.getForObject(url, String::class.java)
		} catch (e: HttpClientErrorException) {
			return ResponseEntity.status(e.statusCode.value()).build()
		}
		return ResponseEntity.ok().body(response)
	}

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = [(ConstraintViolationException::class)])
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: ConstraintViolationException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}