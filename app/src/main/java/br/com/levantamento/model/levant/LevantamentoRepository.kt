package br.com.levantamento.model.levant

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LevantamentoRepository(private val dao: LevantamentoDao) {

    fun insert(levantamento: Levantamento): Long {
        return dao.insert(levantamento)
    }

    fun update(levantamento: Levantamento): Int {
        return dao.update(levantamento)
    }

    fun delete(levantamento: Levantamento): Int {
        return dao.delete(levantamento)
    }

    fun clear(): Int {
        return dao.clearLevantamento()
    }

    suspend fun getLevantamento(key: String): Levantamento{
        return dao.getLevantamento(key)
    }

    //val levantamentos = dao.getAllLevantamentos()
    fun getAllLevantamento(): LiveData<List<Levantamento>> {
        return dao.getAllLevantamentos()
    }

    suspend fun countLevantamento() = withContext(Dispatchers.Default) {
        dao.countLevantamento()
    }

}