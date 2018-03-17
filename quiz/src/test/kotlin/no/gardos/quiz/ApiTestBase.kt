package no.gardos.quiz

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.gardos.quiz.model.dto.CategoryDto
import no.gardos.quiz.model.dto.QuestionDto
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

	fun createGenericCategory(name:String) : Long{
		val category = CategoryDto(name)

		return RestAssured.given().contentType(ContentType.JSON)
				.body(category)
				.post(CATEGORY_PATH)
				.then()
				.statusCode(201)
				.extract().asString().toLong()
	}

	fun createGenericQuestion(categoryId : Long): Long {
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
	}


}