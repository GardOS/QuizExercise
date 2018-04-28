package no.gardos.quiz.apiTest

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.gardos.schema.QuizDto
import org.junit.Test

class QuizApiTest : ApiTestBase() {

	@Test
	fun getQuizzes_ValidQuiz_QuizzesReturned() {
		createGenericQuiz("Quiz1")
		createGenericQuiz("Quiz2")

		val response = RestAssured.given().get(QUIZ_PATH)
				.then()
				.statusCode(200)
				.extract()
				.`as`(Array<QuizDto>::class.java)
				.toList()

		assert(response.count() == 2)
	}

	@Test
	fun createQuiz_ValidQuiz_Created() {
		createGenericQuiz("Quiz")
	}

	@Test
	fun createQuiz_IdSpecified_BadRequest() {
		val quiz = QuizDto(id = 1, name = "Quiz")

		RestAssured.given().contentType(ContentType.JSON)
				.body(quiz)
				.post(QUIZ_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuiz_DuplicateQuiz_Conflict() {
		createGenericQuiz("Quiz")
		val quiz = QuizDto(name = "Quiz")

		RestAssured.given().contentType(ContentType.JSON)
				.body(quiz)
				.post(QUIZ_PATH)
				.then()
				.statusCode(409)
	}

	@Test
	fun createQuiz_NullName_BadRequest() {
		val quiz = QuizDto(name = null)

		RestAssured.given().contentType(ContentType.JSON)
				.body(quiz)
				.post(QUIZ_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuiz_EmptyName_BadRequest() {
		val quiz = QuizDto(name = "")

		RestAssured.given().contentType(ContentType.JSON)
				.body(quiz)
				.post(QUIZ_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuiz_QuestionDoNotExist_BadRequest() {
		val quiz = QuizDto(name = "Quiz", questions = listOf(1234, 4321))

		RestAssured.given().contentType(ContentType.JSON)
				.body(quiz)
				.post(QUIZ_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun getQuiz_QuizExists_Ok() {
		val quiz = createGenericQuiz("Quiz")

		RestAssured.given().pathParam("id", quiz)
				.get("$QUIZ_PATH/{id}")
				.then()
				.statusCode(200)
	}

	@Test
	fun getQuiz_InvalidId_BadRequest() {
		RestAssured.given().pathParam("id", " ")
				.get("$QUIZ_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun getQuiz_QuizDoesNotExist_NotFound() {
		RestAssured.given().pathParam("id", 1234)
				.get("$QUIZ_PATH/{id}")
				.then()
				.statusCode(404)
	}

	@Test
	fun updateQuestion_NewQuestionValid_Ok() {
		val category = createGenericCategory("Category")
		val question = createGenericQuestion(category)
		val oldQuiz = createGenericQuiz("OldQuiz")
		val newQuiz = QuizDto(name = "NewQuiz", questions = listOf(question))

		RestAssured.given().pathParam("id", oldQuiz)
				.contentType(ContentType.JSON)
				.body(newQuiz)
				.put("$QUIZ_PATH/{id}")
				.then()
				.statusCode(200)
	}

	@Test
	fun updateQuiz_InvalidId_BadRequest() {
		val quiz = createGenericQuiz("Quiz")

		RestAssured.given().pathParam("id", " ")
				.contentType(ContentType.JSON)
				.body(quiz)
				.put("$QUIZ_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun updateQuiz_QuizDoesNotExist_NotFound() {
		val quiz = QuizDto(name = "Quiz")

		RestAssured.given().pathParam("id", 1234)
				.contentType(ContentType.JSON)
				.body(quiz)
				.put("$QUIZ_PATH/{id}")
				.then()
				.statusCode(404)
	}

	@Test
	fun updateQuiz_IdInBodyIsNotNull_BadRequest() {
		val oldQuiz = createGenericQuiz("OldQuiz")
		val newQuiz = QuizDto(id = 1234, name = "NewQuiz")

		RestAssured.given().pathParam("id", oldQuiz)
				.contentType(ContentType.JSON)
				.body(newQuiz)
				.put("$QUIZ_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun updateQuiz_NewQuestionDoesNotExist_BadRequest() {
		val oldQuiz = createGenericQuiz("OldQuiz")
		val newQuiz = QuizDto(name = "NewQuiz", questions = listOf(1234))

		RestAssured.given().pathParam("id", oldQuiz)
				.contentType(ContentType.JSON)
				.body(newQuiz)
				.put("$QUIZ_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun deleteQuiz_QuizExists_NoContent() {
		val quiz = createGenericQuiz("Quiz")

		RestAssured.given().pathParam("id", quiz)
				.delete("$QUIZ_PATH/{id}")
				.then()
				.statusCode(204)
	}

	@Test
	fun deleteQuiz_InvalidId_BadRequest() {
		RestAssured.given().pathParam("id", " ")
				.delete("$QUIZ_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun deleteQuiz_QuizDoesNotExist_NotFound() {
		RestAssured.given().pathParam("id", 1234)
				.delete("$QUIZ_PATH/{id}")
				.then()
				.statusCode(404)
	}
}