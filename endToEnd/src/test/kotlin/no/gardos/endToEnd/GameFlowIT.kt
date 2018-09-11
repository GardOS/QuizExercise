package no.gardos.endToEnd

import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import no.gardos.schema.GameStateDto
import no.gardos.schema.QuestionDto
import no.gardos.schema.QuizDto
import org.awaitility.Awaitility.await
import org.awaitility.Duration
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit

//FIXME
@Ignore //Unstable test. Travis cant handle it
class GameFlowIT {
	companion object {
		class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)

		@ClassRule
		@JvmField
		val env = KDockerComposeContainer(File("../docker-compose.yml")).withLocalCompose(true)

		@BeforeClass
		@JvmStatic
		fun initialize() {
			RestAssured.baseURI = "http://localhost"
			RestAssured.port = 8080
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

			await().atMost(300, TimeUnit.SECONDS)
					.pollInterval(Duration.FIVE_SECONDS)
					.ignoreExceptions()
					.until({
						// zuul and eureka is up when 200 is returned
						// this will in itself act as a test proving both zuul and eureka works
						given().get("http://localhost/game-server/health").then().body("status", equalTo("UP"))
						given().get("http://localhost/quiz-server/health").then().body("status", equalTo("UP"))
						// need to make sure the data is created before running this tests
						given().get("http://localhost/quiz-server/quizzes").then().body("size()", equalTo(3))

						true
					})
			authenticate()
		}

		fun authenticate() {
			val token = given().contentType(ContentType.URLENC)
					.formParam("username", "username")
					.formParam("password", "password")
					.post("/signIn")
					.then()
					.statusCode(403)
					.extract().cookie("XSRF-TOKEN")

			val session = given().contentType(ContentType.URLENC)
					.formParam("username", "username")
					.formParam("password", "password")
					.header("X-XSRF-TOKEN", token)
					.cookie("XSRF-TOKEN", token)
					.post("/signIn")
					.then()
					.statusCode(204)
					.extract().cookie("SESSION")

			RestAssured.requestSpecification = RequestSpecBuilder()
					.setAccept(ContentType.JSON)
					.setContentType(ContentType.JSON)
					.addHeader("X-XSRF-TOKEN", token)
					.addCookie("XSRF-TOKEN", token)
					.addCookie("SESSION", Pair(session, token).first)
					.build()
		}
	}

	@Test
	fun completeGame() {
		val quiz = get("/quiz-server/quizzes").`as`(Array<QuizDto>::class.java).first()
		assertEquals("Quiz about letters", quiz.name)
		val gameState = GameStateDto(quiz.id)

		val gameId = given().body(gameState)
				.post("/game-server/games/new-game")
				.then()
				.statusCode(200)
				.extract()
				.path<String>("id")

		val firstQuestion = get("/game-server/games/$gameId/current-question")
				.`as`(QuestionDto::class.java)

		//Guess 1
		val correctAnswerResponse = patch("/game-server/games/$gameId?answer=1")
				.then()
				.statusCode(200)
				.extract()
				.response()
				.body.asString()

		assertEquals("Correct", correctAnswerResponse)

		val secondQuestion = get("/game-server/games/$gameId/current-question")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.`as`(QuestionDto::class.java)

		assertNotEquals(firstQuestion.id, secondQuestion.id)

		given().get("http://localhost/score-server/scores").then().body("size()", equalTo(0))

		//Guess 2
		val wrongAnswerResponse = patch("/game-server/games/$gameId?answer=1")
				.then()
				.statusCode(200)
				.extract()
				.response()
				.body.asString()

		assertEquals("Wrong", wrongAnswerResponse)

		//Guess 3
		patch("/game-server/games/$gameId?answer=1")
				.then()
				.statusCode(204)

		val isDone = get("/game-server/games/$gameId/current-question")
				.then()
				.statusCode(200)
				.extract()
				.path<Boolean>("finished")

		assertTrue(isDone)

		Thread.sleep(3000)

		given().get("http://localhost/score-server/scores").then().body("size()", equalTo(1))


	}
}
