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
		var PlayerOne: Long? = null,
		@get:NotNull
		var PlayerTwo: Long? = null,
		@get:NotNull
		var PlayerOneScore: Int? = null,
		@get:NotNull
		var PlayerTwoScore: Int? = null,
		@get:NotNull
		var RoundNumber: Int? = null,
		@get:Id @get:GeneratedValue
		var id: Long? = null
)