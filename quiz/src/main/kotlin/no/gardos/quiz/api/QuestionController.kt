package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.*
import no.gardos.schema.QuestionDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import org.hibernate.exception.ConstraintViolationException as HibernateConstraintViolationException
import javax.validation.ConstraintViolationException as JavaxConstraintViolationException

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
			category = categoryRepo.findOne(dto.category!!.id!!.toLong())
					?: return ResponseEntity.status(400)
					.body("Category with id: ${dto.category} not found")
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
			pathId: Long
	): ResponseEntity<Any> {
		val question = questionRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Question with id: $pathId not found")

		return ResponseEntity.ok(QuestionConverter.transform(question))
	}

	@ApiOperation("Update an existing question")
	@PutMapping(path = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
	fun updateQuestion(
			@ApiParam("Id of question")
			@PathVariable("id")
			pathId: Long,
			@ApiParam("The new question which will replace the old one")
			@RequestBody
			requestDto: QuestionDto
	): ResponseEntity<Any> {
		if (requestDto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (!questionRepo.exists(pathId))
			return ResponseEntity.status(404).body("Question with id: $pathId not found")

		var category: Category? = null

		if (requestDto.category != null) {
			category = categoryRepo.findOne(requestDto.category!!.id!!.toLong())
					?: return ResponseEntity.status(400)
					.body("Category with id: ${requestDto.category} not found")
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
			pathId: Long
	): ResponseEntity<Any> {
		if (!questionRepo.exists(pathId))
			return ResponseEntity.status(404).body("Question with id: $pathId not found")

		questionRepo.delete(pathId)

		return ResponseEntity.status(204).build()
	}

	@ApiOperation("Get questions by category Id")
	@GetMapping(path = ["/category/{id}"])
	fun getQuestionsByCategoryId(
			@ApiParam("Id of category")
			@PathVariable("id")
			pathId: Long
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

	//Catches validation errors and returns error status based on error
	@ExceptionHandler(value = ([JavaxConstraintViolationException::class, HibernateConstraintViolationException::class,
		DataIntegrityViolationException::class, TransactionSystemException::class]))
	fun handleValidationFailure(ex: Exception, response: HttpServletResponse): String {
		var cause: Throwable? = ex
		for (i in 0..4) { //Iterate 5 times max, since it might have infinite depth
			if (cause is JavaxConstraintViolationException || cause is HibernateConstraintViolationException) {
				response.status = HttpStatus.BAD_REQUEST.value()
				return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
			}
			cause = cause?.cause
		}
		response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
		return "Something went wrong processing the request.  Error:\n${ex.message ?: "Error not found"}"
	}
}