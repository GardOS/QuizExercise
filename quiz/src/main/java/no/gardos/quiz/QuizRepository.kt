package no.gardos.quiz

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : CrudRepository<QuizEntity, Long>{}