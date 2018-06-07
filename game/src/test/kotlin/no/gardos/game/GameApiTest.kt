package no.gardos.game

import com.github.tomakehurst.wiremock.client.WireMock
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.game.model.GameState
import no.gardos.schema.GameStateDto
import org.junit.Test

class GameApiTest : ApiTestBase() {
	@Test
	fun newGame_IdSpecified_BadRequest() {
		val gameState = GameState(id = 1234)

		given().contentType(ContentType.JSON)
				.body(gameState)
				.post("/new-game")
				.then()
				.statusCode(400)
	}

	@Test
	fun newGame_ValidQuiz_Ok() {
		val gameState = GameStateDto(quiz = "1234")
		val json = mockQuizJsonString()

		wireMockServer.stubFor(
				WireMock.get(
						WireMock.urlMatching("/quizzes/.*"))
						.willReturn(WireMock.aResponse()
								.withHeader("Content-Type", "application/json")
								.withBody(json)))

		given().contentType(ContentType.JSON)
				.body(gameState)
				.post("/new-game")
				.then()
				.statusCode(200)
	}
}