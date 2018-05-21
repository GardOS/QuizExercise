package no.gardos.game

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.game.model.GameState
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [(GameApplication::class)])
class GameApiTest {

	@LocalServerPort
	protected var port = 0

	val GAME_PATH = "/games"

	@Before
	fun initialize() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
	}

	@Test
	fun newGame_IdSpecified_BadRequest() {
		val gameState = GameState(id = 1234)

		given().contentType(ContentType.JSON)
				.body(gameState)
				.post(GAME_PATH)
				.then()
				.statusCode(400)
	}
}