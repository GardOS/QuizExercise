package no.gardos.game.model.repository

import no.gardos.game.model.entity.GameState
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface GameStateRepository : CrudRepository<GameState, Long>, GameStateRepositoryCustom

@Transactional
interface GameStateRepositoryCustom