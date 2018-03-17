package no.gardos.quiz

import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test

class QuestionApiTest : ApiTestBase() {

	@Test
	fun testAddQuestion() {
		given().get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(0))

		val category = createGenericCategory("Category")
		createGenericQuestion(category)

		given().get(QUESTION_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(1))
	}
}