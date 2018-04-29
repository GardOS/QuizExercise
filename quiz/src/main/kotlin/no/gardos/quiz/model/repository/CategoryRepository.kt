package no.gardos.quiz.model.repository

import no.gardos.quiz.model.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface CategoryRepository : JpaRepository<Category, Long>, CategoryRepositoryCustom {
	fun findByName(name: String): Category?
	fun findByQuestionsIsNotNull(): List<Category>
}

@Transactional
interface CategoryRepositoryCustom

