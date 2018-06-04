package no.gardos.game

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = [(GameApplication::class)])
@ActiveProfiles("test")
abstract class ApiTestBase {

	companion object {
		lateinit var wireMockServer: WireMockServer

		@BeforeClass
		@JvmStatic
		fun initClass() {
			RestAssured.baseURI = "http://localhost"
			RestAssured.port = 8080
			RestAssured.basePath = "/games"
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
			RestAssured.authentication = RestAssured.basic("testUser", "pwd")

			wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8099).notifier(ConsoleNotifier(true)))
			wireMockServer.start()
		}

		@AfterClass
		@JvmStatic
		fun tearDown() {
			wireMockServer.stop()
		}
	}

	fun mockQuizJsonString(): String {
		return """{"id": "1"}"""
	}

	fun mockFullQuizJsonString(): String {
		return """{"id":1,"name":"Quiz about letters","questions":[
			{"id":1,"questionText":"Which letter is B?","answers":["A","B","C","D"],"correctAnswer":1,"category":{"name":"Letters","id":1}},
			{"id":2,"questionText":"Which letter is not D?","answers":["L","D","D","D"],"correctAnswer":0,"category":{"name":"Letters","id":1}}
			]}""".trimIndent()
	}
}