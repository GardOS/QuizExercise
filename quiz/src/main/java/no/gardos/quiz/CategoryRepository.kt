package no.gardos.quiz

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
interface CategoryRepository : CrudRepository<CategoryEntity, Long>, CategoryRepositoryCustom

@Transactional
interface CategoryRepositoryCustom {
    fun findAllQuizzesWithCategory(id: Long?) : List<QuizEntity>?
}

open class CategoryRepositoryImpl : CategoryRepositoryCustom {
    @PersistenceContext
    private lateinit var em: EntityManager

    //TODO: Fix.
    override fun findAllQuizzesWithCategory(id: Long?) : List<QuizEntity>? {
        var category = em.find(CategoryEntity::class.java, id) ?: return null
        return category.quizEntities
    }
}

