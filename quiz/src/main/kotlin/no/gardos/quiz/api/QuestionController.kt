package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.converter.QuestionConverter
import no.gardos.quiz.model.dto.QuestionDto
import no.gardos.quiz.model.entity.CategoryEntity
import no.gardos.quiz.model.entity.QuestionEntity
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.repository.QuestionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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

		var category: CategoryEntity? = null

		if (dto.category != null) {
			category = categoryRepo.findOne(dto.category) ?: return ResponseEntity.status(400).build()
		}

		val question: QuestionEntity?

		question = questionRepo.save(
				QuestionEntity(
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

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = [(ConstraintViolationException::class)])
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: ConstraintViolationException): String {
		val messages = StringBuilder()

		for (violation in ex.constraintViolations) {
			messages.append(violation.message + "\n")
		}

		return messages.toString()
	}
}