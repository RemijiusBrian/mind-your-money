package dev.ridill.mym.expenses.domain.repository

import dev.ridill.mym.expenses.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagsRepository {

    fun getAllTags(): Flow<List<Tag>>
}