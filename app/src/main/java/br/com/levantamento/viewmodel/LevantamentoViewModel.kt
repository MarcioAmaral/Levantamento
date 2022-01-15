package br.com.levantamento.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.levantamento.model.levant.Levantamento
import br.com.levantamento.model.levant.LevantamentoRepository
import br.com.levantamento.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LevantamentoViewModel<totReg>(private val repository: LevantamentoRepository) : ViewModel(),
    Observable {

    lateinit var levantamentos: LiveData<List<Levantamento>>
  //  val inventarios = repository.getAllInventario()

    var item: Levantamento? = null

    var isUpdateOrDelete = false

    var totReg = 0

    companion object {
        var baseAlterada = false    // Controle imp e manutenção base SQLite
    }

    var idCod = MutableLiveData<Boolean>()

    @Bindable
    val inputCodigo = MutableLiveData<String?>()

    @Bindable
    val inputDescricao = MutableLiveData<String>()

    @Bindable
    val inputCont1 = MutableLiveData<String>()

    @Bindable
    val inputCont2 = MutableLiveData<String>()

    @Bindable
    val inputCont3 = MutableLiveData<String>()

    @Bindable
    val inputFabr = MutableLiveData<String>()

    @Bindable
    val inputValid = MutableLiveData<String>()

    @Bindable
    val inputTecnico = MutableLiveData<String>()

    @Bindable
    val inputFoto = MutableLiveData<String>()

    @Bindable
    var btnSalvar = MutableLiveData<Boolean>()

    @Bindable
    var btnFoto = MutableLiveData<Boolean>()

    @Bindable
    var btnIncluir = MutableLiveData<Boolean>()

    @Bindable
    var btnExcluir = MutableLiveData<Boolean>()

    @Bindable
    var btnCancelar = MutableLiveData<Boolean>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    var spLocal = MutableLiveData<String>()
    var spUnidMed = MutableLiveData<String>()
    var spEnd1 = MutableLiveData<String>()
    var spEnd2 = MutableLiveData<String>()
    var spEnd3 = MutableLiveData<String>()
    var spEnd4 = MutableLiveData<String>()
    var spEnd5 = MutableLiveData<String>()
    var spEnd6 = MutableLiveData<String>()

    init {
        cancelarItem()
    }

    fun cancelarItem() { //Configura botões tela
        clearAll()
        idCod.value = true
        btnCancelar.value = false
        btnSalvar.value = false
        btnFoto.value = false
        btnIncluir.value = true
        btnExcluir.value = false
        isUpdateOrDelete = false
    }

    fun clearAll() {  //Limpa tela
        inputCodigo.value = null
        //empresa: Int
        //unid.negócio: String
        spLocal.value = null
        inputDescricao.value = null
        spUnidMed.value = null
        spEnd1.value = ""
        spEnd2.value = ""
        spEnd3.value = ""
        spEnd4.value = ""
        spEnd5.value = ""
        spEnd6.value = ""
        inputFabr.value = ""
        inputValid.value = ""
        inputCont1.value = ""
        inputCont2.value = ""
        inputCont3.value = ""
        //N.Fiscal: String
        //valor: Double
        inputTecnico.value = ""
        inputFoto.value = ""
    }

    fun incluirItem() {  //Configura botões tela
        clearAll()
        idCod.value = true
        btnSalvar.value = true
        btnFoto.value = true
        btnCancelar.value = true
        btnIncluir.value = false
        btnExcluir.value = false
        isUpdateOrDelete = false
    }

    fun editarExcluir() { //Configura botões tela
        idCod.value = false
        btnSalvar.value = true
        btnFoto.value = true
        btnCancelar.value = true
        btnIncluir.value = false
        btnExcluir.value = true
        isUpdateOrDelete = true
    }

    fun telaExcluir() {  //Ajustar tela(proteger campos) antes da confirmação da exclusão
        idCod.value = false
        btnCancelar.value = false
        btnSalvar.value = false
        btnFoto.value = false
        btnIncluir.value = true
        btnExcluir.value = false
    }

    fun saveOrUpdate() = if (inputCodigo.value.isNullOrBlank()) {
        statusMessage.value = Event("Por favor informe o código")
    } else if (spLocal.value.isNullOrBlank()) {
        statusMessage.value = Event("Por favor informe o local")
    } else if (inputDescricao.value.isNullOrBlank()) {
        statusMessage.value = Event("Por favor informe a descrição")
    } else if (inputTecnico.value.isNullOrBlank()) {
        statusMessage.value = Event("Por favor informe o técnico")
    } else if (spUnidMed.value.isNullOrBlank()) {
        statusMessage.value = Event("Por favor informe a unidade")
    } else {
        if (isUpdateOrDelete) {
            alterarItem(getLevantamento())
        } else {
            inserirItem(getLevantamento())
        }
        posicionarItemSalvo()
    }

    private fun inserirItem(levantamento: Levantamento) = viewModelScope.launch {
        var newRowId: Long
        withContext(Dispatchers.IO) {
            newRowId = repository.insert(levantamento)
        }
        if (newRowId > -1) {
            statusMessage.value = Event("Item ${levantamento.levantamentoId} inserido com sucesso!")
        } else {
            statusMessage.value = Event("Ocorreu um problema, item não inserido!")
        }
    }

    private fun posicionarItemSalvo() {
        clearAll()
        btnSalvar.value = false
        btnCancelar.value = false
        btnFoto.value = false
        btnIncluir.value = true
        btnExcluir.value = false
        isUpdateOrDelete = false
    }

    private fun getLevantamento(): Levantamento {
        return Levantamento(
            levantamentoId = inputCodigo.value!!.trim(),
            empresa = 0,
            unid_negocio = "",
            local = spLocal.value!!,
            descricao = inputDescricao.value!!.trim(),
            unid_medida = spUnidMed.value!!,
            endereco1 = spEnd1.value!!,
            endereco2 = spEnd2.value!!,
            endereco3 = spEnd3.value!!,
            endereco4 = spEnd4.value!!,
            endereco5 = spEnd5.value!!,
            endereco6 = spEnd6.value!!,
            fabricacao = inputFabr.value!!.trim(),
            validade = inputValid.value!!.trim(),
            contagem1 = inputCont1.value!!.trim(),
            contagem2 = inputCont2.value!!.trim(),
            contagem3 = inputCont3.value!!.trim(),
            nt_fiscal = "",
            valor = 0.00,
            tecnico = inputTecnico.value!!.trim(),
            foto = inputFoto.value?.trim()
        )
    }

    private fun alterarItem(levantamento: Levantamento) = viewModelScope.launch {
        var newRowId: Int
        withContext(Dispatchers.IO) {
            newRowId = repository.update(levantamento)
        }
        if (newRowId > -1) {
            statusMessage.value = Event("Item ${levantamento.levantamentoId} atualizado com sucesso!")
        } else {
            statusMessage.value = Event("Ocorreu um problema, item não atualizado!")
        }
    }

    fun excluirItem(item: Levantamento) = viewModelScope.launch {
        var itemExcl: Int
        val id = item.levantamentoId
        withContext(Dispatchers.IO) {
            itemExcl = repository.delete(item)
        }
        if (itemExcl > -1) {
            statusMessage.value = Event("Item ${id} excluído com sucesso!")
        } else {
            statusMessage.value = Event("Ocorreu um problema, item não excluído!")
        }
    }

    fun initUpadetAndDelete(levantamento: Levantamento) {  //Atualizar campos tela
        inputCodigo.value = levantamento.levantamentoId
        inputDescricao.value = levantamento.descricao
        inputCont1.value = levantamento.contagem1
        inputCont2.value = levantamento.contagem2
        inputCont3.value = levantamento.contagem3
        inputFabr.value = levantamento.fabricacao
        inputValid.value = levantamento.validade
        inputFoto.value = when (!levantamento.foto.isNullOrBlank()) {
            true -> levantamento.foto
            else -> null
        }
        // if(!inventario.foto.isNullOrBlank()) inputFoto.value = inventario.foto
        inputTecnico.value = levantamento.tecnico
        isUpdateOrDelete = true
    }

    fun countReg(): Job = viewModelScope.launch {
        val cursorCount = repository.countLevantamento()
        cursorCount.moveToFirst()
        totReg = cursorCount.getInt(0)
        //  "Foram exportados ${cursorCount.getInt(0).toString()} registro(s) para a tabela inventario.xls"

    }

    suspend fun limparLevantamento() = withContext(Dispatchers.IO) {
        repository.clear()  // limpar base Inventário
    }

    fun loadInventarios() = viewModelScope.launch() {
        levantamentos = repository.getAllLevantamento()
    }

    fun controlarBD(status: Boolean) {
        baseAlterada = status
    }

    fun getItem(key: String): Job = viewModelScope.launch {
        item = repository.getLevantamento(key)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}
