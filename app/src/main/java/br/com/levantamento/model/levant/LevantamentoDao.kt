package br.com.levantamento.model.levant

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LevantamentoDao {
    @Insert
    fun insert(levantamento: Levantamento): Long

    @Update
    fun update(levantamento: Levantamento): Int

    @Delete
    fun delete(levantamento: Levantamento): Int

    @Query("DELETE FROM levantamento WHERE levantamentoId = :key")
    fun deleteKey(key: String)

    @Query("DELETE FROM levantamento")
    fun clearLevantamento(): Int

    @Query("SELECT * FROM levantamento WHERE levantamentoId = :key")
    suspend fun getLevantamento(key: String): Levantamento

    @Query("SELECT * FROM levantamento ORDER BY levantamentoId DESC")
    fun getAllLevantamentos(): LiveData<List<Levantamento>>

    @Query("SELECT COUNT(*) FROM levantamento")
    fun countLevantamento(): Cursor

}