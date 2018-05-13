package no.gardos.player.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface PlayerRepository : JpaRepository<Player, Long>, PlayerRepositoryCustom

@Transactional
interface PlayerRepositoryCustom

