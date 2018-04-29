package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.converter.QuizConverter
import no.gardos.quiz.model.entity.Question
import no.gardos.quiz.model.entity.Quiz
import no.gardos.quiz.model.repository.QuestionRepository
import no.gardos.quiz.model.repository.QuizRepository
import no.gardos.schema.QuizDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
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
	): ResponseEntity<Any> {
		//Id is auto-generated and should not be specified
		if (dto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (quizRepo.findByName(dto.name.toString()) != null)
			return ResponseEntity.status(409).body("Name is already taken")

		val questions = dto.questions?.map {
			questionRepo.findById(it!!).orElse(null)
					?: return ResponseEntity.status(400).body("Question with id: $it not found")
		}

		val quiz = quizRepo.save(Quiz(name = dto.name, questions = questions))

		return ResponseEntity.status(201).body(quiz.id)
	}

	@ApiOperation("Update an existing quiz")
	@PutMapping(path = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
	fun updateQuiz(
			@ApiParam("Id of quiz")
			@PathVariable("id")
			pathId: Long?,
			@ApiParam("The new quiz which will replace the old one")
			@RequestBody
			requestDto: QuizDto
	): ResponseEntity<Any> {
		if (requestDto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (pathId == null) {
			return ResponseEntity.status(400).body("Invalid Id in path")
		}

		if (!quizRepo.existsById(pathId)) {
			return ResponseEntity.status(404).body("Quiz with id: $pathId not found")
		}

		var newQuestions: List<Question>? = null

		if (requestDto.questions != null) {
			newQuestions = requestDto.questions?.map {
				questionRepo.findById(it!!).orElse(null)
						?: return ResponseEntity.status(400).body("Question with id: $it not found")
			}
		}

		val newQuestion = quizRepo.save(
				Quiz(
						id = pathId,
						name = requestDto.name,
						questions = newQuestions
				)
		)

		return ResponseEntity.ok(QuizConverter.transform(newQuestion))
	}

	@ApiOperation("Get a quiz by ID")
	@GetMapping(path = ["/{id}"])
	fun getQuiz(
			@ApiParam("Id of quiz")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<Any> {
		if (pathId == null) {
			return ResponseEntity.status(400).body("Invalid Id in path")
		}

		val optQuiz = quizRepo.findById(pathId)

		if (!optQuiz.isPresent) {
			return ResponseEntity.status(404).body("Quiz with id: $pathId not found")
		}

		return ResponseEntity.ok(QuizConverter.transform(optQuiz.get()))
	}

	@ApiOperation("Get a quiz by ID")
	@DeleteMapping(path = ["/{id}"])
	fun deleteQuiz(
			@ApiParam("Id of quiz")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<Any> {
		if (pathId == null) {
			return ResponseEntity.status(400).body("Invalid Id in path")
		}

		val optQuiz = quizRepo.findById(pathId)

		if (!optQuiz.isPresent) {
			return ResponseEntity.status(404).body("Quiz with id: $pathId not found")
		}

		quizRepo.deleteById(pathId)

		return ResponseEntity.status(204).build()
	}

	/*
	Catches validation errors and returns 400 instead of 500
	Because of wrapping and black-boxing beyond my understanding and patience, whenever a
	ConstraintViolationException is thrown it might be wrapped to something else based on the context.
	Although messy.. below is the best effort to keep this in check.
	See: https://stackoverflow.com/a/45118680
	The downside to this "solution" is that there might be Exceptions which are not from constraints being thrown, which
	warrants a 500 status code instead, which is very misleading.
	*/
	@ExceptionHandler(value = ([ConstraintViolationException::class, DataIntegrityViolationException::class,
		TransactionSystemException::class]))
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: RuntimeException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}