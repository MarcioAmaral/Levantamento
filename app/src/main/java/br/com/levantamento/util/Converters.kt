package br.com.levantamento.util

import androidx.room.TypeConverter
import java.util.*

class Converters {
    companion object {
        @TypeConverter
        fun toDate(dateLong: Long?): Date? {
            return dateLong?.let { Date(it) }
        }

        @TypeConverter
        fun fromDate(date: Date?): Long? {
            return date?.time?.toLong()
        }
    }
}