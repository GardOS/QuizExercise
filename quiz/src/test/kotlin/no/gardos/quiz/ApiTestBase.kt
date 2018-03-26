package no.gardos.quiz

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.quiz.model.dto.CategoryDto
import no.gardos.quiz.model.dto.QuestionDto
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [(QuizApplication::class)])
abstract class ApiTestBase {

	@LocalServerPort
	protected var port = 0

	val QUESTION_PATH = "/questions"
	val CATEGORY_PATH = "/categories"

	fun createGenericCategory(name: String): Long {
		val category = CategoryDto(name)

		return RestAssured.given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(201)
				.extract().asString().toLong()
	}

	fun createGenericQuestion(categoryId: Long): Long {
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = categoryId
		)

		return RestAssured.given().contentType(ContentType.JSON)
				.body(question)
				.post("/questions")
				.then()
				.statusCode(201)
				.extract().asString().toLong()
	}

	@Before
	@After
	fun clean() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.basePath = "/quizrest/api"
		RestAssured.port = port
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

		removeCategories() //Todo: remove foreign keys before deletion
	}

	fun removeCategories() {
		val list = given().accept(ContentType.JSON).get(CATEGORY_PATH)
				.then()
				.statusCode(200)
				.extract()
				.`as`(Array<CategoryDto>::class.java)
				.toList()

		list.stream().forEach {
			given().pathParam("id", it.id)
					.delete("$CATEGORY_PATH/{id}")
					.then()
					.statusCode(204)
		}

		given().get(CATEGORY_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(0))
	}
}