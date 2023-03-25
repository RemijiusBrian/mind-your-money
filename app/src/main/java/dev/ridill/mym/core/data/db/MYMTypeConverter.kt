package dev.ridill.mym.core.data.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DateConverter {
    @TypeConverter
    fun fromTimestamp(millis: Long?): LocalDateTime? =
        millis?.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }

    @TypeConverter
    fun dateToTimestamp(dateTime: LocalDateTime?): Long? =
        dateTime?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
}