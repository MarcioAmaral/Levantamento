package br.com.levantamento.model.levant

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Levantamento(
    @PrimaryKey
    var levantamentoId: String,
    val empresa: Int,
    val unid_negocio: String?,
    val local: String,
    val descricao: String,
    val unid_medida: String,
    val endereco1: String?,
    val endereco2: String?,
    val endereco3: String?,
    val endereco4: String?,
    val endereco5: String?,
    val endereco6: String?,
    /*@TypeConverters(Converters::class)
    val fabricacao: Long = 0L,
    @TypeConverters(Converters::class)
    val validade: Long = 0L,
    @TypeConverters(Converters::class)
    val contagem1: Long = 0L,
    @TypeConverters(Converters::class)
    val contagem2: Long = 0L,
    @TypeConverters(Converters::class)
    val contagem3: Long = 0L,*/
    val fabricacao: String,
    val validade: String,
    val contagem1: String,
    val contagem2: String,
    val contagem3: String,
    val nt_fiscal: String?,
    val valor: Double,
    val tecnico: String,
    val foto: String?
)