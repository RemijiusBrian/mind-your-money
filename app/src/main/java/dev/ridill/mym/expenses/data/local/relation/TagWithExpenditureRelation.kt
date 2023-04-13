package dev.ridill.mym.expenses.data.local.relation

data class TagWithExpenditureRelation(
    val tag: String,
    val colorCode: Int?,
    val expenditure: Double
)
