package no.gardos.quiz

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.quiz.model.dto.QuestionDto
import org.junit.Test

class QuestionApiTest : ApiTestBase() {

	@Test
	fun getQuizzes_ValidQuizzes_QuizzesReturned() {
		val category = createGenericCategory("Category")
		createGenericQuestion(category)
		createGenericQuestion(category)

		val response = given().get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.extract()
				.`as`(Array<QuestionDto>::class.java)
				.toList()

		assert(response.count() == 2)
	}

	@Test
	fun createQuestion_ValidQuestion_Created() {
		val category = createGenericCategory("Category")
		createGenericQuestion(category)
	}

	@Test
	fun createQuestion_IdSpecified_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				id = 1234,
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_NullQuestionText_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = null,
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_EmptyQuestionText_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = "",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_TooFewAnswers_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("2"),
				correctAnswer = 0,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_TooManyAnswers_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3", "4"),
				correctAnswer = 0,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_NullCorrectAnswer_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = null,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_TooSmallCorrectAnswer_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = -1,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_TooBigCorrectAnswer_BadRequest() {
		val category = createGenericCategory("Category")
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 4,
				category = category
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun createQuestion_NullCategory_Created() {
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = null
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(201)
	}

	@Test
	fun createQuestion_CategoryDoNotExist_BadRequest() {
		val question = QuestionDto(
				questionText = "What is 1+1?",
				answers = listOf("0", "1", "2", "3"),
				correctAnswer = 2,
				category = 1234
		)

		given().contentType(ContentType.JSON)
				.body(question)
				.post(QUESTION_PATH)
				.then()
				.statusCode(400)
	}

	@Test
	fun getQuestion_QuestionExists_Ok() {
		val category = createGenericCategory("Category")
		val question = createGenericQuestion(category)

		given().pathParam("id", question)
				.get("$QUESTION_PATH/{id}")
				.then()
				.statusCode(200)
	}

	@Test
	fun getQuestion_InvalidId_BadRequest() {
		given().pathParam("id", " ")
				.get("$QUESTION_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun getQuestion_QuestionDoesNotExist_NotFound() {
		given().pathParam("id", 1234)
				.get("$QUESTION_PATH/{id}")
				.then()
				.statusCode(404)
	}

	@Test
	fun deleteQuestion_QuestionExists_NoContent() {
		val category = createGenericCategory("Category")
		val question = createGenericQuestion(category)

		given().pathParam("id", question)
				.delete("$QUESTION_PATH/{id}")
				.then()
				.statusCode(204)
	}

	@Test
	fun deleteQuestion_InvalidId_BadRequest() {
		given().pathParam("id", " ")
				.delete("$QUESTION_PATH/{id}")
				.then()
				.statusCode(400)
	}

	@Test
	fun deleteQuestion_QuestionDoesNotExist_NotFound() {
		given().pathParam("id", 1234)
				.delete("$QUESTION_PATH/{id}")
				.then()
				.statusCode(404)
	}
}