package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.converter.QuestionConverter
import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.entity.Question
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.repository.QuestionRepository
import no.gardos.schema.QuestionDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

@Api(value = "/questions", description = "API for questions.")
@RequestMapping(
		path = ["/questions"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class QuestionController {
	@Autowired
	lateinit var questionRepo: QuestionRepository

	@Autowired
	lateinit var categoryRepo: CategoryRepository

	@ApiOperation("Retrieve all questions")
	@GetMapping
	fun getAllQuestions(): ResponseEntity<List<QuestionDto>> {
		return ResponseEntity.ok(QuestionConverter.transform(questionRepo.findAll()))
	}

	@ApiOperation("Create a question")
	@PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
	@ApiResponse(code = 201, message = "The id of newly created question")
	fun createQuestion(
			@ApiParam("Should not specify id")
			@RequestBody
			dto: QuestionDto
	): ResponseEntity<Any> {
		//Id is auto-generated and should not be specified
		if (dto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		var category: Category? = null

		if (dto.category != null) {
			val optCategory = categoryRepo.findById(dto.category!!.id!!)

			if (!optCategory.isPresent) {
				return ResponseEntity.status(400).body("Category with id: ${dto.category} not found")
			}

			category = optCategory.get()
		}

		val question: Question?

		question = questionRepo.save(
				Question(
						questionText = dto.questionText,
						answers = dto.answers,
						correctAnswer = dto.correctAnswer,
						category = category
				)
		)

		return ResponseEntity.status(201).body(question.id)
	}

	@ApiOperation("Get a question by ID")
	@GetMapping(path = ["/{id}"])
	fun getQuestion(
			@ApiParam("Id of question")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<Any> {
		if (pathId == null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		val optQuestion = questionRepo.findById(pathId)

		if (!optQuestion.isPresent) {
			return ResponseEntity.status(404).body("Question with id: $pathId not found")
		}

		return ResponseEntity.ok(QuestionConverter.transform(optQuestion.get()))
	}

	@ApiOperation("Update an existing question")
	@PutMapping(path = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
	fun updateQuestion(
			@ApiParam("Id of question")
			@PathVariable("id")
			pathId: Long?,
			@ApiParam("The new question which will replace the old one")
			@RequestBody
			requestDto: QuestionDto
	): ResponseEntity<Any> {
		if (requestDto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (pathId == null) {
			return ResponseEntity.status(400).body("Invalid Id in path") //Todo: Pointless?
		}

		val optQuestion = questionRepo.findById(pathId)

		if (!optQuestion.isPresent) {
			return ResponseEntity.status(404).body("Question with id: $pathId not found")
		}

		var category: Category? = null

		if (requestDto.category != null) {
			val optCategory = categoryRepo.findById(requestDto.category!!.id!!)

			if (!optCategory.isPresent) {
				return ResponseEntity.status(400).body("Category with id: ${requestDto.category} not found")
			}

			category = optCategory.get()
		}

		val newQuestion = questionRepo.save(
				Question(
						id = pathId,
						questionText = requestDto.questionText,
						answers = requestDto.answers,
						correctAnswer = requestDto.correctAnswer,
						category = category
				)
		)

		return ResponseEntity.ok(QuestionConverter.transform(newQuestion))
	}

	@ApiOperation("Delete a question")
	@DeleteMapping(path = ["/{id}"])
	fun deleteQuestion(
			@ApiParam("Id of question")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<Any> {
		if (pathId == null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		val optQuestion = questionRepo.findById(pathId)

		if (!optQuestion.isPresent) {
			return ResponseEntity.status(404).body("Question with id: $pathId not found")
		}

		questionRepo.deleteById(pathId)

		return ResponseEntity.status(204).build()
	}

	@ApiOperation("Get questions by category Id")
	@GetMapping(path = ["/category/{id}"])
	fun getQuestionsByCategoryId(
			@ApiParam("Id of category")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<Any> {
		return ResponseEntity.ok(QuestionConverter.transform(questionRepo.findQuestionByCategoryId(pathId)))
	}

	@ApiOperation("Get questions by category name")
	@GetMapping(path = ["/category/name/{name}"])
	fun getQuestionsByCategoryName(
			@ApiParam("Name of category")
			@PathVariable("name")
			pathName: String?
	): ResponseEntity<Any> {
		return ResponseEntity.ok(QuestionConverter.transform(questionRepo.findQuestionByCategoryName(pathName)))
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