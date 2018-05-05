package no.gardos.game.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.gardos.game.model.converter.GameStateConverter
import no.gardos.game.model.entity.GameState
import no.gardos.game.model.repository.GameStateRepository
import no.gardos.schema.GameStateDto
import no.gardos.schema.QuestionDto
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

@Api(value = "/games", description = "API for interacting with the game.")
@RequestMapping(
		path = ["/games"],
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

	@ApiOperation("Get a game by ID")
	@GetMapping(path = ["/{id}"])
	fun getGame(
			@ApiParam("Id of the game")
			@PathVariable("id")
			pathId: Long
	): ResponseEntity<Any> {
		val optGame = gameStateRepo.findById(pathId)
		if (!optGame.isPresent) {
			return ResponseEntity.status(404).body("Game with id: $pathId not found")
		}

		return ResponseEntity.ok(optGame.get())
	}

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

		if (dto.quiz == null || dto.player == null) {
			return ResponseEntity.status(400).body("Invalid request. References invalid")
		}

		val quiz: QuizDto?

		try {
			val url = "$quizServerPath/${dto.quiz}"
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

		return ResponseEntity.ok(GameStateConverter.transform(gameState))
	}

	@ApiOperation("Get current question")
	@GetMapping(path = ["/{id}/current-question"])
	fun currentQuestion(
			@ApiParam("Id of the game")
			@PathVariable("id")
			pathId: Long
	): ResponseEntity<Any> {
		val optGame = gameStateRepo.findById(pathId)
		if (!optGame.isPresent) {
			return ResponseEntity.status(404).body("Game with id: $pathId not found")
		}

		val game = optGame.get()

		if (game.isFinished) return ResponseEntity.ok().body(game)


		val question: QuestionDto
		try {
			val quizUrl = "$quizServerPath/${game.Quiz}"
			val quiz = rest.getForObject(quizUrl, QuizDto::class.java) //Todo: Make api-method for question?
			question = quiz?.questions?.getOrNull(game.RoundNumber)
					?: return ResponseEntity.status(500).body("Could not find question")
		} catch (ex: HttpClientErrorException) {
			return ResponseEntity.status(ex.statusCode).body(ex.responseBodyAsString)
		}

		return ResponseEntity.ok(question)
	}


	@ApiOperation("Guess answer on the current question in the game session")
	@PatchMapping(path = ["/{id}"])
	fun guess(
			@ApiParam("Id of the game")
			@PathVariable("id")
			pathId: Long,
			@RequestParam("answer", required = true)
			answer: Int?
	): ResponseEntity<Any> {
		if (answer == null || answer !in 0..3) {
			return ResponseEntity.status(400).body("Invalid parameters")
		}

		//Todo: Check if the correct player is playing

		val optGame = gameStateRepo.findById(pathId)
		if (!optGame.isPresent) return ResponseEntity.status(404).body("Game with id: $pathId not found")
		val game = optGame.get()

		if (game.isFinished) return ResponseEntity.ok().body(game)

		val quiz: QuizDto?
		try {
			val quizUrl = "$quizServerPath/${game.Quiz}"
			quiz = rest.getForObject(quizUrl, QuizDto::class.java)
		} catch (ex: HttpClientErrorException) {
			game.RoundNumber++ //Instead of being stuck on this question, skip it
			gameStateRepo.save(game)
			return ResponseEntity.status(ex.statusCode).body(ex.responseBodyAsString)
		}

		val correctAnswer = quiz?.questions?.getOrNull(game.RoundNumber)?.correctAnswer

		if (correctAnswer == null) {
			game.RoundNumber++ //Instead of being stuck on this question, skip it
			gameStateRepo.save(game)
			return ResponseEntity.status(500).body("Could not find answer to question")
		}

		val isCorrect = answer == correctAnswer

		game.RoundNumber++
		if (isCorrect) game.Score++
		if (game.RoundNumber >= quiz.questions!!.count()) {
			game.isFinished = true
		}

		gameStateRepo.save(game)

		return if (isCorrect) ResponseEntity.ok().body("Correct") else ResponseEntity.ok().body("Wrong")
	}

	//Catches validation errors and returns 400 instead of 500
	@ExceptionHandler(value = ([ConstraintViolationException::class, DataIntegrityViolationException::class]))
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	fun handleValidationFailure(ex: RuntimeException): String {
		return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
	}
}