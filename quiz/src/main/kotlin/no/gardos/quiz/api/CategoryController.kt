package no.gardos.quiz.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.dto.CategoryConverter
import no.gardos.quiz.model.dto.CategoryDto
import no.gardos.quiz.model.entity.CategoryEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.hibernate.exception.ConstraintViolationException

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
	fun getAllCategories(): ResponseEntity<List<CategoryDto>> {
		return ResponseEntity.ok(CategoryConverter.transform(repo.findAll()))
	}

	@ApiOperation("Create a category")
	@PostMapping(consumes = [(MediaType.APPLICATION_JSON_VALUE)])
	@ApiResponse(code = 201, message = "The id of newly created category")
	fun createCategory(
			@ApiParam("Category name. Should not specify id")
			@RequestBody
			dto: CategoryDto): ResponseEntity<Long> {

		//Auto-generated
		if (dto.id != null) {
			return ResponseEntity.status(400).build()
		}

		if (dto.name.isNullOrEmpty()) {
			return ResponseEntity.status(400).build()
		}

		val category: CategoryEntity?
		try {
			category = repo.save(CategoryEntity(name = dto.name!!))
		} catch (e: ConstraintViolationException) {
			return ResponseEntity.status(409).build()
		}

		return ResponseEntity.status(201).body(category.id)
	}

	/*
	Every time a ConstraintViolationException is thrown, instead
	of ending up in a 500 error, we catch it are return 400.

	Important: we also need to add @Validated on this class.
    */
	@ExceptionHandler(value = [(ConstraintViolationException::class)])
	@ResponseStatus(value = HttpStatus.CONFLICT)
	fun handleValidationFailure(ex: ConstraintViolationException): String {
		return "Error: Category with that name already exists."
	}
}