package no.gardos.game.model.converter

import no.gardos.game.model.entity.GameState
import no.gardos.schema.GameStateDto

class GameStateConverter {
	companion object {
		fun transform(gameState: GameState): GameStateDto {
			return GameStateDto(
					id = gameState.id,
					Quiz = gameState.Quiz,
					Player = gameState.Player
			)
		}

		fun transform(gameStates: Iterable<GameState>): List<GameStateDto> {
			return gameStates.map { transform(it) }
		}
	}
}