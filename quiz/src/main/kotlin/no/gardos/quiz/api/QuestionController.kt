package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.dto.QuestionConverter
import no.gardos.quiz.model.dto.QuestionDto
import no.gardos.quiz.model.entity.QuestionEntity
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.repository.QuestionRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Api(value = "/questions", description = "API for questions.")
@RequestMapping(
		path = ["/questions"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class QuestionController {
	@Autowired
	lateinit var questionRepository: QuestionRepository

	@Autowired
	lateinit var categoryRepository: CategoryRepository

	@ApiOperation("Retrieve all questions")
	@GetMapping
	fun getAllQuestions(): ResponseEntity<List<QuestionDto>> {
		return ResponseEntity.ok(QuestionConverter.transform(questionRepository.findAll()))
	}

	@ApiOperation("Create a question")
	@PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
	@ApiResponse(code = 201, message = "The id of newly created category")
	fun createCategory(
			@ApiParam("Should not specify id")
			@RequestBody
			dto: QuestionDto): ResponseEntity<Long> {

		//Auto-generated
		if (dto.id != null) {
			return ResponseEntity.status(400).build()
		}

		if (dto.questionText == null || dto.answers == null || dto.correctAnswer == null || dto.category == null) {
			return ResponseEntity.status(400).build()
		}

		val category = categoryRepository.findOne(dto.category!!)
		if (category == null) {
			return ResponseEntity.status(400).build()
		}

		val question: QuestionEntity?
		try {
			question = questionRepository.save(
					QuestionEntity(
							questionText = dto.questionText,
							answers = dto.answers,
							correctAnswer = dto.correctAnswer,
							category = category
					)
			)
		} catch (e: ConstraintViolationException) {
			return ResponseEntity.status(400).build()
		}

		return ResponseEntity.status(201).body(question.id)
	}
}