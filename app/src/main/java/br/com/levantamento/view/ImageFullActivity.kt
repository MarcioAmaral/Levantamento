package br.com.levantamento.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.levantamento.R
import br.com.levantamento.databinding.ActivityImageFullBinding
import br.com.levantamento.model.levant.LevantamentoDatabase
import br.com.levantamento.model.levant.LevantamentoRepository
import br.com.levantamento.util.GlideApp
import br.com.levantamento.viewmodel.LevantamentoViewModelFactory
import br.com.levantamento.viewmodel.LevantamentoViewModel

class ImageFullActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageFullBinding
    private lateinit var levantamentoViewModel: LevantamentoViewModel<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_full)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val dao = LevantamentoDatabase.getInstance(application).levantamentoDao
        val repository = LevantamentoRepository(dao)
        val factory = LevantamentoViewModelFactory(repository)
        levantamentoViewModel = ViewModelProvider(this, factory).get(LevantamentoViewModel::class.java) as LevantamentoViewModel<Any?>

        binding.viewModel = levantamentoViewModel
        binding.lifecycleOwner = this

        levantamentoViewModel.inputCodigo.value = intent.getStringExtra("codigo")
        levantamentoViewModel.inputDescricao.value = intent.getStringExtra("descricao")
      //  levantamentoViewModel.inputFoto.value = intent.getStringExtra("foto")
        val currentPhotoPath = intent.getStringExtra("foto")
        if (!currentPhotoPath.isNullOrEmpty()) {
            GlideApp.with(this)
                .load(currentPhotoPath)
                .into(binding.imageView)
        }

        toolbar.setNavigationOnClickListener {
            // setSupportActionBar(toolbar)
            finish()
        }
    }
}