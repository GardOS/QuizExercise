package no.gardos.quiz

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.quiz.model.dto.CategoryDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class CategoryApiTest : ApiTestBase() {

	@Test
	fun getCategories_ValidCategories_CategoriesReturned() {
		createGenericCategory("Category1")
		createGenericCategory("Category2")

		val response = given().get(CATEGORY_PATH)
				.then()
				.statusCode(200)
				.extract()
				.`as`(Array<CategoryDto>::class.java)
				.toList()

		assert(response.stream().anyMatch { c -> c.name == "Category1" })
		assert(response.stream().anyMatch { c -> c.name == "Category2" })
	}

	@Test
	fun createCategory_ValidCategory_Created() {
		createGenericCategory("Category")
	}

	@Test
	fun createCategory_IdSpecified_BadRequest() {
		val category = CategoryDto("Name", id = 1)

		given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createCategory_NullName_BadRequest() {
		val category = CategoryDto(null)

		given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createCategory_EmptyName_BadRequest() {
		val category = CategoryDto("")

		given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createCategory_DuplicateCategory_Conflict() {
		createGenericCategory("Category")
		val category = CategoryDto("Category")

		given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(409)
	}

	@Test
	fun createCategory_ConstraintError_BadRequest() {
		val category = CategoryDto("123456789012345678901234567890123") //33 in length

		given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun getCategory_CategoryExists_Ok() {
		val category = createGenericCategory("Category")

		given().pathParam("id", category)
				.get("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(200)
	}

	@Test
	fun getCategory_InvalidId_BadRequest() {
		given().pathParam("id", " ")
				.get("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun getCategory_CategoryDoesNotExist_NotFound() {
		given().pathParam("id", 1234)
				.get("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(404)
	}

	@Test
	fun updateCategory_CategoryExists_Ok() {
		val category = createGenericCategory("Category")

		given().pathParam("id", category)
				.body("NewCategory")
				.put("$CATEGORY_PATH/{id}/name")
				.then()
				.statusCode(200)
	}

	@Test
	fun updateCategory_InvalidId_BadRequest() {
		given().pathParam("id", " ")
				.body("NewCategory")
				.put("$CATEGORY_PATH/{id}/name")
				.then()
				.statusCode(400)
	}

	@Test
	fun updateCategory_CategoryDoesNotExist_NotFound() {
		given().pathParam("id", 1234)
				.body("NewCategory")
				.put("$CATEGORY_PATH/{id}/name")
				.then()
				.statusCode(404)
	}

	@Test
	fun updateCategory_NameAlreadyInUse_Conflict() {
		val category = createGenericCategory("Category1")
		createGenericCategory("Category2")

		given().pathParam("id", category)
				.body("Category2")
				.put("$CATEGORY_PATH/{id}/name")
				.then()
				.statusCode(409)
	}

	@Test
	fun deleteCategory_CategoryExists_NoContent() {
		val category = createGenericCategory("Category")

		given().pathParam("id", category)
				.delete("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(204)
	}

	@Test
	fun deleteCategory_InvalidId_BadRequest() {
		given().pathParam("id", " ")
				.delete("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun deleteCategory_CategoryDoesNotExist_NotFound() {
		given().pathParam("id", 1234)
				.delete("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(404)
	}


	@Test
	fun deleteCategory_QuestionHasRelation_NoContent() {
		val category = createGenericCategory("Category")
		createGenericQuestion(category)

		given().get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.body("size()", CoreMatchers.equalTo(1))

		given().pathParam("id", category)
				.delete("$CATEGORY_PATH/{id}")
				.then()
				.statusCode(204)

		given().get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.body("size()", CoreMatchers.equalTo(1))
	}
}