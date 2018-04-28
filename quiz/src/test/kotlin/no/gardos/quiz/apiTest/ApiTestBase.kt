package no.gardos.quiz.apiTest

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.quiz.QuizApplication
import no.gardos.schema.CategoryDto
import no.gardos.schema.QuestionDto
import no.gardos.schema.QuizDto
import org.hamcrest.CoreMatchers.equalTo
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

	val QUIZ_PATH = "/quizzes"
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
				.post(QUESTION_PATH)
				.then()
				.statusCode(201)
				.extract().asString().toLong()
	}

	fun createGenericQuiz(name: String): Long {
		val quiz = QuizDto(
				name = name,
				questions = listOf()
		)

		return RestAssured.given().contentType(ContentType.JSON)
				.body(quiz)
				.post(QUIZ_PATH)
				.then()
				.statusCode(201)
				.extract().asString().toLong()
	}

	@Before
	fun clean() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

		//Ensure that the DB has a neutral state before starting any tests
		removeQuizzes()
		removeCategories()
		removeQuestions()
	}

	fun removeQuizzes() {
		val list = given().accept(ContentType.JSON).get(QUIZ_PATH)
				.then()
				.statusCode(200)
				.extract()
				.`as`(Array<QuizDto>::class.java)
				.toList()

		list.stream().forEach {
			given().pathParam("id", it.id)
					.delete("$QUIZ_PATH/{id}")
					.then()
					.statusCode(204)
		}

		given().get(QUIZ_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(0))
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

	fun removeQuestions() {
		val list = given().accept(ContentType.JSON).get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.extract()
				.`as`(Array<QuestionDto>::class.java)
				.toList()

		list.stream().forEach {
			given().pathParam("id", it.id)
					.delete("$QUESTION_PATH/{id}")
					.then()
					.statusCode(204)
		}

		given().get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(0))
	}
}