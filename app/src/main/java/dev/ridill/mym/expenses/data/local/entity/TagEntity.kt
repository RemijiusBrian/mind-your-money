package dev.ridill.mym.expenses.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val colorCode: Int
)