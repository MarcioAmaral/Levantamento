package br.com.levantamento.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.levantamento.R
import br.com.levantamento.databinding.ListItemBinding
import br.com.levantamento.model.levant.Levantamento

class LevantRecyclerViewAdapter(private val clickListener: (Levantamento)->Unit): RecyclerView.Adapter<InvViewHolder>()
{
    private val levantList = ArrayList<Levantamento>()

    companion object {
        var levantSelect: Levantamento? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item,parent,false)
        return InvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvViewHolder, position: Int) {
        holder.bind(levantList[position],clickListener)
    }

    override fun getItemCount() = levantList.size

    fun setList(levantamentos: List<Levantamento>){
        levantList.clear()
        levantList.addAll(levantamentos)
    }
}

class InvViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(levantamento: Levantamento, clickListener: (Levantamento) -> Unit){
        binding.idItem.text = levantamento.levantamentoId
        binding.descrItem.text = levantamento.descricao
        binding.listItemLayout.setOnClickListener {
            LevantRecyclerViewAdapter.levantSelect = levantamento
            clickListener(levantamento)
        }

    }
}