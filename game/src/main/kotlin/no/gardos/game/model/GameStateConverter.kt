package no.gardos.game.model

import no.gardos.schema.GameStateDto

class GameStateConverter {
	companion object {
		fun transform(gameState: GameState): GameStateDto {
			return GameStateDto(
					id = gameState.id.toString(),
					quiz = gameState.Quiz.toString(),
					player = gameState.Player,
					Score = gameState.Score,
					RoundNumber = gameState.RoundNumber,
					isFinished = gameState.isFinished
			)
		}

		fun transform(gameStates: Iterable<GameState>): List<GameStateDto> {
			return gameStates.map { transform(it) }
		}
	}
}