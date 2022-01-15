package br.com.levantamento.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.levantamento.model.levant.LevantamentoRepository

class LevantamentoViewModelFactory(private val repository: LevantamentoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LevantamentoViewModel::class.java)) {
            return LevantamentoViewModel<Any>(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}