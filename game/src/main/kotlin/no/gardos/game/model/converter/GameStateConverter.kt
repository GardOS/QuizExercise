package no.gardos.game.model.converter

import no.gardos.game.model.entity.GameState
import no.gardos.schema.GameStateDto

class GameStateConverter {
	companion object {
		fun transform(gameState: GameState): GameStateDto {
			return GameStateDto(
					id = gameState.id,
					quiz = gameState.Quiz,
					player = gameState.Player,
					isFinished = gameState.isFinished
			)
		}

		fun transform(gameStates: Iterable<GameState>): List<GameStateDto> {
			return gameStates.map { transform(it) }
		}
	}
}