package br.com.levantamento.model.levant

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Levantamento::class], version = 2, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class LevantamentoDatabase : RoomDatabase() {

    abstract val levantamentoDao: LevantamentoDao

    companion object {
        @Volatile
        private var INSTANCE: LevantamentoDatabase? = null
        const val BASE_NOME = "invent_pat_database"

        fun getInstance(context: Context): LevantamentoDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LevantamentoDatabase::class.java,
                        BASE_NOME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}