package no.gardos.quiz

import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test

class CategoryApiTest : ApiTestBase() {

	@Test
	fun testAddCategory() {
		given().get(CATEGORY_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(0))

		createGenericCategory("Category")

		given().get(CATEGORY_PATH)
				.then()
				.statusCode(200)
				.body("size()", equalTo(1))
	}


}