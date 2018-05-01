package no.gardos.game.model.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class GameState(
		@get:NotNull
		var Quiz: Long? = null,
		@get:NotNull
		var Player: Long? = null,
		@get:NotNull
		var Score: Int = 0,
		@get:NotNull
		var RoundNumber: Int = 0,
		@get:Id @get:GeneratedValue
		var id: Long? = null
)