package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.converter.CategoryConverter
import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.repository.QuestionRepository
import no.gardos.schema.CategoryDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

@Api(value = "/categories", description = "API for categories.")
@RequestMapping(
		path = ["/categories"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class CategoryController {
	@Autowired
	private lateinit var categoryRepo: CategoryRepository

	@Autowired
	private lateinit var questionRepo: QuestionRepository

	@ApiOperation("Test Eureka load handling using config values from docker container")
	@GetMapping(path = ["/eureka"])
	fun testEureka(): ResponseEntity<String> {
		val id = (System.getenv("PRODUCER_ID") ?: "Undefined").trim()
		return ResponseEntity.ok(id)
	}

	@ApiOperation("Get all the categories. Add param \"withQuestions\" to only get categories with questions")
	@GetMapping
	fun getCategories(
			@RequestParam("withQuestions", required = false)
			withQuestions: String?
	): ResponseEntity<List<CategoryDto>> {
		if (withQuestions != null)
			return ResponseEntity.ok(CategoryConverter.transform(categoryRepo.findByQuestionsIsNotNull().distinct()))

		return ResponseEntity.ok(CategoryConverter.transform(categoryRepo.findAll()))
	}

	@ApiOperation("Create new category")
	@PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
	@ApiResponse(code = 201, message = "The id of newly created category")
	fun createCategory(
			@ApiParam("Category name. Should not specify id")
			@RequestBody
			dto: CategoryDto
	): ResponseEntity<Long> {
		//Id is auto-generated and should not be specified
		if (dto.id != null) {
			return ResponseEntity.status(400).build()
		}

		if (categoryRepo.findByName(dto.name.toString()) != null)
			return ResponseEntity.status(409).build()

		val category = categoryRepo.save(Category(name = dto.name))

		return ResponseEntity.status(201).body(category.id)
	}

	@ApiOperation("Get a category by ID")
	@GetMapping(path = ["/{id}"])
	fun getCategory(
			@ApiParam("Id of category")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<CategoryDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!categoryRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		val category = categoryRepo.findOne(pathId)

		return ResponseEntity.ok(CategoryConverter.transform(category))
	}

	@ApiOperation("Update name of a category")
	@PatchMapping(path = ["/{id}/name"], consumes = [MediaType.TEXT_PLAIN_VALUE])
	fun updateCategory(
			@ApiParam("Id of Category")
			@PathVariable("id")
			pathId: Long?,
			@ApiParam("The new name which will replace the old one")
			@RequestBody
			newName: String
	): ResponseEntity<CategoryDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!categoryRepo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		if (categoryRepo.findByName(newName) != null)
			return ResponseEntity.status(409).build()

		val category = categoryRepo.findOne(pathId)
		category.name = newName

		val newCategory = categoryRepo.save(category)

		return ResponseEntity.ok(CategoryConverter.transform(newCategory))
	}

	@ApiOperation("Delete a category")
	@DeleteMapping(path = ["/{id}"])
	fun deleteCategory(
			@ApiParam("Id of Category")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<CategoryDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		val category = categoryRepo.findOne(pathId) ?: return ResponseEntity.status(404).build()

		val questions = questionRepo.findQuestionByCategoryId(category.id)

		//If there are any questions with a relation to this category then set FK-column to null before deleting
		if (questions.any()) {
			questions.forEach {
				it.category = null
				questionRepo.save(it)
			}
		}

		categoryRepo.delete(pathId)

		return ResponseEntity.status(204).build()
	}

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = [(ConstraintViolationException::class)])
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: ConstraintViolationException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}