package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.converter.CategoryConverter
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.dto.CategoryDto
import no.gardos.quiz.model.entity.CategoryEntity
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
	private lateinit var repo: CategoryRepository

	@ApiOperation("Get all the categories")
	@GetMapping
	fun getCategories(): ResponseEntity<List<CategoryDto>> {
		return ResponseEntity.ok(CategoryConverter.transform(repo.findAll()))
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

		if (dto.name.isNullOrEmpty()) {
			return ResponseEntity.status(400).build()
		}

		if (repo.findByName(dto.name.toString()) != null)
			return ResponseEntity.status(409).build()

		val category = repo.save(CategoryEntity(name = dto.name!!))

		return ResponseEntity.status(201).body(category.id)
	}

	@ApiOperation("Get a category by ID")
	@GetMapping(path = ["/{id}"])
	fun getCategory(
			@ApiParam("Id of Category")
			@PathVariable("id")
			pathId: Long?
	): ResponseEntity<CategoryDto> {
		if (pathId == null) {
			return ResponseEntity.status(400).build()
		}

		if (!repo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		val category = repo.findOne(pathId)

		return ResponseEntity.ok(CategoryConverter.transform(category))
	}

	@ApiOperation("Update name of a category")
	@PutMapping(path = ["/{id}/name"], consumes = [MediaType.TEXT_PLAIN_VALUE])
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

		if (!repo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		if (repo.findByName(newName) != null)
			return ResponseEntity.status(409).build()

		val newCategory = repo.save(CategoryEntity(id = pathId, name = newName))

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

		if (!repo.exists(pathId)) {
			return ResponseEntity.status(404).build()
		}

		repo.delete(pathId)

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