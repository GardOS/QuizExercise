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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException
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

	@ApiOperation("Get all the categories. Add param \"with-questions\" to only get categories with questions")
	@GetMapping
	fun getCategories(
			@RequestParam("with-questions", required = false)
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
	): ResponseEntity<Any> {
		//Id is auto-generated and should not be specified
		if (dto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (categoryRepo.findByName(dto.name.toString()) != null)
			return ResponseEntity.status(409).body("Name is already taken")

		val category = categoryRepo.save(Category(name = dto.name))

		return ResponseEntity.status(201).body(category.id)
	}

	@ApiOperation("Get a category by ID")
	@GetMapping(path = ["/{id}"])
	fun getCategory(
			@ApiParam("Id of category")
			@PathVariable("id")
			pathId: Long
	): ResponseEntity<Any> {
		val category = categoryRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Category with id: $pathId not found")

		return ResponseEntity.ok(CategoryConverter.transform(category))
	}

	@ApiOperation("Update name of a category")
	@PatchMapping(path = ["/{id}/name"], consumes = [MediaType.TEXT_PLAIN_VALUE])
	fun updateCategory(
			@ApiParam("Id of Category")
			@PathVariable("id")
			pathId: Long,
			@ApiParam("The new name which will replace the old one")
			@RequestBody
			newName: String
	): ResponseEntity<Any> {
		val category = categoryRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Category with id: $pathId not found")

		if (categoryRepo.findByName(newName) != null)
			return ResponseEntity.status(409).body("Name is already taken")

		category.name = newName

		val newCategory = categoryRepo.save(category)

		return ResponseEntity.ok(CategoryConverter.transform(newCategory))
	}

	@ApiOperation("Delete a category")
	@DeleteMapping(path = ["/{id}"])
	fun deleteCategory(
			@ApiParam("Id of Category")
			@PathVariable("id")
			pathId: Long
	): ResponseEntity<Any> {
		val category = categoryRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Category with id: $pathId not found")

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