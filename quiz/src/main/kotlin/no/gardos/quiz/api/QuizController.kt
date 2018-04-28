package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.converter.QuizConverter
import no.gardos.quiz.model.entity.Quiz
import no.gardos.quiz.model.repository.QuestionRepository
import no.gardos.quiz.model.repository.QuizRepository
import no.gardos.schema.QuizDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import javax.validation.ConstraintViolationException

@Api(value = "/quizzes", description = "API for quizzes.")
@RequestMapping(
		path = ["/quizzes"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class QuizController {
	@Autowired
	private lateinit var rest: RestTemplate

	@Autowired
	private lateinit var quizRepo: QuizRepository
	@Autowired
	private lateinit var questionRepo: QuestionRepository

	@ApiOperation("Get all the quizzes")
	@GetMapping
	fun getQuizzes(): ResponseEntity<List<QuizDto>> {
		return ResponseEntity.ok(QuizConverter.transform(quizRepo.findAll()))
	}

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

		if (quizRepo.findByName(dto.name.toString()) != null)
			return ResponseEntity.status(409).build()

		val questions = dto.questions?.map {
			questionRepo.findOne(it) ?: return ResponseEntity.status(400).build()
		}

		val quiz = quizRepo.save(Quiz(name = dto.name, questions = questions))

		return ResponseEntity.status(201).body(quiz.id)
	}

	@ApiOperation("Get a quiz by ID")
	@GetMapping(path = ["/{id}"])
	fun getQuiz(
			@ApiParam("Id of quiz")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<QuizDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!quizRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		val quiz = quizRepo.findOne(pathId)

		return ResponseEntity.ok(QuizConverter.transform(quiz))
	}

	@ApiOperation("Get a quiz by ID")
	@DeleteMapping(path = ["/{id}"])
	fun deleteQuiz(
			@ApiParam("Id of quiz")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<QuizDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!quizRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		quizRepo.delete(pathId)

		return ResponseEntity.status(204).build()
	}

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = ([ConstraintViolationException::class, DataIntegrityViolationException::class]))
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: RuntimeException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}