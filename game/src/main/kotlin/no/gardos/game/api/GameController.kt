package no.gardos.game.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.gardos.game.model.converter.GameStateConverter
import no.gardos.game.model.entity.GameState
import no.gardos.game.model.repository.GameStateRepository
import no.gardos.schema.GameStateDto
import no.gardos.schema.GuessDto
import no.gardos.schema.QuizDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import javax.validation.ConstraintViolationException

@Api(value = "/game", description = "API for categories.")
@RequestMapping(
		path = ["/game"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class GameController {
	@Autowired
	private lateinit var rest: RestTemplate

	@Autowired
	private lateinit var gameStateRepo: GameStateRepository

	@Value("\${quizServerPath}")
	private lateinit var quizServerPath: String

	@ApiOperation("Starts a new game. Initializes a GameState object for tracking the game")
	@PostMapping(path = ["/new-game"])
	fun newGame(
			@ApiParam
			@RequestBody
			dto: GameStateDto
	): ResponseEntity<Any> {
		if (dto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (dto.Quiz == null || dto.Player == null) {
			return ResponseEntity.status(400).body("Invalid request. References invalid")
		}

		val quiz: QuizDto?

		try {
			val url = "$quizServerPath/${dto.Quiz}"
			quiz = rest.getForObject(url, QuizDto::class.java)
		} catch (ex: HttpClientErrorException) {
			return ResponseEntity.status(ex.statusCode).body("Error when querying Quiz:\n ${ex.responseBodyAsString}")
		}

		//Todo: Logic for finding players

		val gameState = gameStateRepo.save(
				GameState(
						Quiz = quiz!!.id,
						Player = 1 //Temp
				)
		)

		//Todo: Return first question of the quiz? How are players starting the game?
//		val question: QuestionDto?
//		try {
//			val url = "$quizServerPath/${dto.Quiz}"
//			question = rest.getForObject(url, QuestionDto::class.java)
//		} catch (ex: HttpClientErrorException) {
//			return ResponseEntity.status(ex.statusCode).body("Error when querying Question:\n ${ex
//					.responseBodyAsString}")
//		}

		return ResponseEntity.ok(GameStateConverter.transform(gameState))
	}

	@ApiOperation("")
	@PatchMapping(path = ["/guess"])
	fun guess(
			@ApiParam
			@RequestBody
			dto: GuessDto
	): ResponseEntity<Any> {
		if (dto.Game == null || dto.Answer == null) {
			return ResponseEntity.status(400).body("Invalid request. References invalid")
		}

		val optGame = gameStateRepo.findById(dto.Game!!)
		if (!optGame.isPresent) {
			return ResponseEntity.status(404).body("Game with id: ${dto.Game} not found")
		}
		val game = optGame.get()

		//Todo: Check if the correct player is playing

		val quiz: QuizDto?
		try {
			val url = "$quizServerPath/${game.Quiz}"
			quiz = rest.getForObject(url, QuizDto::class.java)
		} catch (ex: HttpClientErrorException) {
			return ResponseEntity.status(ex.statusCode).body("Error when querying Quiz:\n ${ex.responseBodyAsString}")
		}

		val question = quiz?.questions?.getOrNull(game.RoundNumber)

		if (question?.correctAnswer == dto.Answer) {
			return ResponseEntity.ok().body("Correct")
		}
		return ResponseEntity.ok().body("Wrong")
	}

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = ([ConstraintViolationException::class, DataIntegrityViolationException::class]))
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: RuntimeException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}