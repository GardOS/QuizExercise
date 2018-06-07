package no.gardos.score.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ScoreRepository : CrudRepository<Score, Long>