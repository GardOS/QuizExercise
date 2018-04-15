package no.gardos.quiz.api

import io.swagger.annotations.Api
import no.gardos.quiz.model.repository.QuizRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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
	private lateinit var quizRepo: QuizRepository

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = [(ConstraintViolationException::class)])
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: ConstraintViolationException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}