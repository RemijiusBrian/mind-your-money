package dev.ridill.mym.expenses.data.repository

import dev.ridill.mym.core.util.DispatcherProvider
import dev.ridill.mym.expenses.data.local.TagsDao
import dev.ridill.mym.expenses.data.local.entity.TagEntity
import dev.ridill.mym.expenses.domain.model.Tag
import dev.ridill.mym.expenses.domain.model.TagInput
import dev.ridill.mym.expenses.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TagsRepositoryImpl(
    private val dao: TagsDao,
    private val dispatcherProvider: DispatcherProvider
) : TagsRepository {

    override fun getAllTags(): Flow<List<Tag>> = dao.getAllTags().map { entities ->
        entities.map(TagEntity::toTag)
    }

    override suspend fun insert(tag: TagInput) = withContext(dispatcherProvider.io) {
        dao.insert(tag.toEntity())
    }
}