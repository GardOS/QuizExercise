package no.gardos.game.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface GameStateRepository : JpaRepository<GameState, Long>, GameStateRepositoryCustom

@Transactional
interface GameStateRepositoryCustom