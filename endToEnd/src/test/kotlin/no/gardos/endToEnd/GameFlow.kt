package no.gardos.endToEnd

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.schema.GameStateDto
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit

class GameFlow {
	companion object {

		class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)

		@ClassRule
		@JvmField
		val env = KDockerComposeContainer(File("../docker-compose.yml")).withLocalCompose(true)

		@BeforeClass
		@JvmStatic
		fun initialize() {
			RestAssured.baseURI = "http://localhost"
			RestAssured.port = 80
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

			await().atMost(400, TimeUnit.SECONDS)
					.ignoreExceptions()
					.until({
						RestAssured.given()
								.get("http://localhost/user")
								.then().statusCode(401)
						true
					})
		}
	}

	@Test
	fun getUser_NoSession_Unauthorized() {
		RestAssured.given().get("/user")
				.then()
				.statusCode(401)
	}

	@Test
	fun getUser_WithSession_Ok() {
		val username = "username"
		val password = "password"

		val token = RestAssured.given().contentType(ContentType.URLENC)
				.formParam("username", username)
				.formParam("password", password)
				.post("/signIn")
				.then()
				.statusCode(403)
				.extract().cookie("XSRF-TOKEN")

		val session = RestAssured.given().contentType(ContentType.URLENC)
				.formParam("username", username)
				.formParam("password", password)
				.header("X-XSRF-TOKEN", token)
				.cookie("XSRF-TOKEN", token)
				.post("/signIn")
				.then()
				.statusCode(204)
				.extract().cookie("SESSION")

		RestAssured.given().cookie("SESSION", Pair(session, token).first)
				.get("/user")
				.then()
				.statusCode(200)
				.body("name", CoreMatchers.equalTo(username))
				.body("roles", Matchers.contains("ROLE_USER"))
	}

	@Test
	fun completeGame() {
		val username = "username"
		val password = "password"

		val token = given().contentType(ContentType.URLENC)
				.formParam("username", username)
				.formParam("password", password)
				.post("/signIn")
				.then()
				.statusCode(403)
				.extract().cookie("XSRF-TOKEN")

		val session = given().contentType(ContentType.URLENC)
				.formParam("username", username)
				.formParam("password", password)
				.header("X-XSRF-TOKEN", token)
				.cookie("XSRF-TOKEN", token)
				.post("/signIn")
				.then()
				.statusCode(204)
				.extract().cookie("SESSION")

		RestAssured.given().cookie("SESSION", Pair(session, token).first)
				.get("/user")
				.then()
				.statusCode(200)
				.body("name", CoreMatchers.equalTo(username))
				.body("roles", Matchers.contains("ROLE_USER"))

		val gameState = GameStateDto(quiz = 14)

		await().atMost(60, TimeUnit.SECONDS)
				.ignoreExceptions()
				.until({
					given().accept(ContentType.JSON)
							.contentType(ContentType.JSON)
							.header("X-XSRF-TOKEN", token)
							.cookie("XSRF-TOKEN", token)
							.cookie("SESSION", Pair(session, token).first)
							.body(gameState)
							.post("/game-server/games/new-game")
							.then()
							.statusCode(200)
					true
				})

	}
}