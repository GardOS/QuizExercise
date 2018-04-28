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
	): ResponseEntity<Long> {
		//Id is auto-generated and should not be specified
		if (dto.id != null) {
			return ResponseEntity.status(400).build()
		}

		var category: Category? = null

		if (dto.category != null) {
			category = categoryRepo.findOne(dto.category) ?: return ResponseEntity.status(400).build()
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
	): ResponseEntity<QuestionDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!questionRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		val question = questionRepo.findOne(pathId)

		return ResponseEntity.ok(QuestionConverter.transform(question))
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
	): ResponseEntity<QuestionDto> {
		if (requestDto.id != null) {
			return ResponseEntity.status(400).build()
		}

		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!questionRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		var newCategory: Category? = null

		if (requestDto.category != null) {
			newCategory = categoryRepo.findOne(requestDto.category) ?: return ResponseEntity.status(400).build()
		}

		val newQuestion = questionRepo.save(
				Question(
						id = pathId,
						questionText = requestDto.questionText,
						answers = requestDto.answers,
						correctAnswer = requestDto.correctAnswer,
						category = newCategory
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
	): ResponseEntity<QuestionDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!questionRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		questionRepo.delete(pathId)

		return ResponseEntity.status(204).build()
	}

	@ApiOperation("Get questions by category Id")
	@GetMapping(path = ["/category/{id}"])
	fun getQuestionsByCategoryId(
			@ApiParam("Id of category")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<List<QuestionDto>> {
		return ResponseEntity.ok(QuestionConverter.transform(questionRepo.findQuestionByCategoryId(pathId)))
	}

	@ApiOperation("Get questions by category name")
	@GetMapping(path = ["/category/name/{name}"])
	fun getQuestionsByCategoryName(
			@ApiParam("Name of category")
			@PathVariable("name")
			pathName: String?
	): ResponseEntity<List<QuestionDto>> {
		return ResponseEntity.ok(QuestionConverter.transform(questionRepo.findQuestionByCategoryName(pathName)))
	}

	/*
	Catches validation errors and returns 400 instead of 500
	Although messy.. TransactionSystemException is included as there are some cases where a
	ConstraintViolationException is thrown, but Spring interprets it as a TransactionSystemException.
	See: https://stackoverflow.com/a/45118680
	The downside to this "solution" is that there might be actual TransactionSystemExceptions being thrown, which
	warrants a 500 status code instead, which is very misleading.
	*/
	@ExceptionHandler(value = ([ConstraintViolationException::class, TransactionSystemException::class]))
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: RuntimeException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}