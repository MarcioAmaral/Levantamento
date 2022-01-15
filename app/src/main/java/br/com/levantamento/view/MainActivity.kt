package br.com.levantamento.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.levantamento.viewmodel.LevantamentoViewModel
import br.com.levantamento.R
import br.com.levantamento.databinding.ActivityMainBinding
import br.com.levantamento.model.levant.Levantamento
import br.com.levantamento.model.levant.LevantamentoDatabase
import br.com.levantamento.model.levant.LevantamentoRepository
import br.com.levantamento.util.Util
import br.com.levantamento.viewmodel.LevantamentoViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var layout_main: LinearLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var levantamentoViewModel: LevantamentoViewModel<Any?>
    private lateinit var adapterLevViewHolder: LevantRecyclerViewAdapter
   // private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout

   // private val CAMREQUEST = 1
    private val PICK_IMAGE_CODE = 1
  //  private val PICK_IMAGE = 1
  //  var fileUri: Uri? = null
    var currentPhotoPath = ""
    //lateinit var bitFotoSelecionada: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
       // Thread.sleep(1000)
       // setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        layout_main = findViewById(R.id.mainLayout)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        GlobalScope.launch(Dispatchers.Main) {
            navView.setNavigationItemSelectedListener {
                intent = when (it.itemId) {
                    R.id.nav_export -> Intent(this@MainActivity, DrawerActivity::class.java).apply {
                        putExtra("opcao", "export")
                    }

                    R.id.nav_import -> Intent(this@MainActivity, DrawerActivity::class.java).apply {
                        putExtra("opcao", "import")
                    }

                    R.id.nav_clear -> Intent(this@MainActivity, DrawerActivity::class.java).apply {
                        putExtra("opcao", "clearAll")
                    }

                    else -> intent
                }
                //   toolbar.setNavigationIcon(R.drawable.ic_lock)  // Trocar icon da drawer

                startActivity(intent)
             //   finish(

                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }

        val dao = LevantamentoDatabase.getInstance(application).levantamentoDao
        val repository = LevantamentoRepository(dao)
        val factory = LevantamentoViewModelFactory(repository)
        levantamentoViewModel = ViewModelProvider(this, factory).get(LevantamentoViewModel::class.java) as LevantamentoViewModel<Any?>
        binding.viewModel = levantamentoViewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        levantamentoViewModel.message.observe(this, {
            it.getContentIfNotHandled()?.let { its ->
                Util.showSnackBar(layout_main, its)
            }
        })

        loadSpinner()

        binding.spinnerLocal.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    levantamentoViewModel.spLocal.value = parent.getItemAtPosition(position).toString()
                    
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.spinnerUnid.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {

                    levantamentoViewModel.spUnidMed.value = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.spinnerEnd1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    levantamentoViewModel.spEnd1.value = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.spinnerEnd2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    levantamentoViewModel.spEnd2.value = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        levantamentoViewModel.spLocal.observe(this, {
            if (it.isNullOrBlank()) {
                clearSpinner()
            }
        })

        levantamentoViewModel.inputCodigo.observe(this, {
            // buscar na base e alimentar adapterLevViewHolder.levantSelect = levantamento
            if (!it.isNullOrBlank() && levantamentoViewModel.btnIncluir.value == true) {
                levantamentoViewModel.getItem(it)
                if (levantamentoViewModel.item != null) {
                    LevantRecyclerViewAdapter.levantSelect = levantamentoViewModel.item
                    levantamentoViewModel.editarExcluir()
                    listItemClicked(levantamentoViewModel.item!!)
                }
            }
            if (!it.isNullOrBlank() && levantamentoViewModel.isUpdateOrDelete &&
                adapterLevViewHolder.itemCount != 0
            ) {
                var spinnerKey =
                    LevantRecyclerViewAdapter.levantSelect?.local?.substringBefore("-")?.trim()
                var posicao = getIndexByString(binding.spinnerLocal, spinnerKey!!)
                binding.spinnerLocal.setSelection(posicao)

                spinnerKey =
                    LevantRecyclerViewAdapter.levantSelect?.unid_medida?.substringBefore("-")
                        ?.trim()
                posicao = getIndexByString(binding.spinnerUnid, spinnerKey!!)
                binding.spinnerUnid.setSelection(posicao)

                spinnerKey =
                    LevantRecyclerViewAdapter.levantSelect?.endereco1.toString().substringBefore(
                        "-"
                    ).trim()
                posicao = getIndexByString(binding.spinnerEnd1, spinnerKey)
                binding.spinnerEnd1.setSelection(posicao)

                spinnerKey =
                    LevantRecyclerViewAdapter.levantSelect?.endereco2.toString().substringBefore(
                        "-"
                    ).trim()
                posicao = getIndexByString(binding.spinnerEnd2, spinnerKey)
                binding.spinnerEnd2.setSelection(posicao)
            }
        })
        binding.imageView.setOnClickListener {
            val currentPhotoPath = levantamentoViewModel.inputFoto.value
            if (!currentPhotoPath.isNullOrEmpty()) {
                intent = Intent(this@MainActivity, ImageFullActivity::class.java)
                intent.putExtra("codigo", levantamentoViewModel.inputCodigo.value)
                intent.putExtra("descricao", levantamentoViewModel.inputDescricao.value)
                intent.putExtra("foto", currentPhotoPath)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getIndexByString(spinner: Spinner, keySpinner: String): Int {
        var index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().substringBefore("-").trim().equals(
                    keySpinner,
                    ignoreCase = true
                )) {
                index = i
                break
            }
        }
        return index
    }

    private fun clearSpinner() {
        binding.spinnerLocal.setSelection(0)
        binding.spinnerUnid.setSelection(0)
       /* binding.spinnerEnd1.setSelection(0)
        binding.spinnerEnd2.setSelection(0) */
        binding.spinnerLocal.requestFocus(0)

    }

    fun initRecyclerView() {
        binding.inventarioRecyclerView.layoutManager = LinearLayoutManager(this)
        adapterLevViewHolder = LevantRecyclerViewAdapter { selectedItem: Levantamento ->
            listItemClicked(
                selectedItem
            )
        }
        binding.inventarioRecyclerView.adapter = adapterLevViewHolder
        levantamentoViewModel.loadInventarios()
        displayInvList()
    }

    fun listItemClicked(levantamento: Levantamento) {
        levantamentoViewModel.editarExcluir()
        levantamentoViewModel.initUpadetAndDelete(levantamento)
    }

    fun displayInvList() {
        levantamentoViewModel.levantamentos.observe(this, {
            adapterLevViewHolder.setList(it)
            adapterLevViewHolder.notifyDataSetChanged()
        })
    }

    fun loadSpinner(){
        //spinnerLoadUnid()
        var myStrings = arrayOf(
            "011 - kg",
            "001 - litro",
            "002 - unidade",
            "017 - caixa"
        )
        spinnerLoad(myStrings, R.id.spinnerUnid)

        //spinnerLoadEnd1()
        myStrings = arrayOf(
            "019 - Rua 19",
            "030 - Rua Principal",
            "002 - Rua da Prata"
        )
        spinnerLoad(myStrings, R.id.spinnerEnd1)

        //spinnerLoadEnd2()
        myStrings = arrayOf(
            "001 - Coluna 1",
            "030 - Coluna 30",
            "002 - Segunda coluna"
        )
        spinnerLoad(myStrings, R.id.spinnerEnd2)

        //spinnerLoadEnd3()
        myStrings = arrayOf(
            "001 - Prateleira 1010",
            "020 - Prateleira 1121",
            "054 - Prateleira 89"
        )
        spinnerLoad(myStrings, R.id.spinnerEnd3)

        //spinnerLoadEnd4()
        myStrings = arrayOf("089 - Palete 89", "099 - Palete 99", "100 - Palete 100")
        spinnerLoad(myStrings, R.id.spinnerEnd4)

        //spinnerLoadEnd5()
        myStrings = arrayOf(
            "550 - Cx. 550",
            "867 - Cx. 867",
            "300 - Cx. 300",
            "089 - Cx. 089"
        )
        spinnerLoad(myStrings, R.id.spinnerEnd5)

        //spinnerLoadEnd6()
        myStrings = arrayOf("005 - Cabide 5", "020 - Cabide 20", "054 - Cabide 54")
        spinnerLoad(myStrings, R.id.spinnerEnd6)

        // spinnerLoadLocal()
        myStrings = arrayOf(
            "Selecionar o Local",
            "001 - Matriz",
            "010 - Filial 10",
            "015 - Filial 15"
        )
        spinnerLoad(myStrings, R.id.spinnerLocal)
    }

    fun spinnerLoad(arrayInf: Array<String>, spinner: Int) {
        //fonte de dados
        //Adapter for spinner
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayInf
        )
        val tempSpinner: Spinner = findViewById(spinner)

        tempSpinner.adapter = arrayAdapter
    }

    fun abrirCamera(view: View) {   // Camera
        if (binding.idCodigo.text.isNullOrBlank()) {
            Util.showSnackBar(
                view,
                "Por favor informe o código do item antes da foto"
            )
            binding.idCodigo.requestFocus(0)
            return
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                // Continue only if the File was successfully created
                try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "br.com.levantamento",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, PICK_IMAGE_CODE)
                    levantamentoViewModel.inputFoto.value = photoURI.toString()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("ddMMyyy_HHmmss").format(Date())
        val storageDir: File? = createPicturesPath("LevantPat")

        return File.createTempFile(
            "IMG${binding.idCodigo.text}_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun createPicturesPath(directory: String): File? {
        var mediaStorageDir: File? = null
        try {
            mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), directory)
            // Cria a pasta se não existir
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()){
                    Util.showSnackBar(layout_main, "")
                    return null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mediaStorageDir
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when(requestCode){
                PICK_IMAGE_CODE ->
                    if (resultCode == Activity.RESULT_OK) {
                        if (!currentPhotoPath.isEmpty()) {
                            Glide.with(this)
                                .load(currentPhotoPath)
                                .into(binding.imageView)
                        }
                    }
            }
        }catch (e: Exception){
            e.printStackTrace()

        }
    }

    override fun onResume() {
        super.onResume()
        val totRecycler = adapterLevViewHolder.itemCount
        levantamentoViewModel.countReg()
        val totBase = levantamentoViewModel.totReg
        if (totRecycler != totBase && LevantamentoViewModel.baseAlterada) {
            withStyle()
        }
    }

    fun withStyle() {

        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                this,
                android.R.style.Holo_SegmentedButton
            )
        )

        with(builder)
        {
            setTitle("Alteração na base!")
            setMessage(
                "Atenção, as informações da base Levantamento sofreram alterações. " +
                        "O aplicativo vai ser fechado. Favor abri-lo novamente"
            )
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))

            show()
        }
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        finish()
    }

    fun excluirItem(view: View){

        val item: String? = LevantRecyclerViewAdapter.levantSelect?.levantamentoId
        levantamentoViewModel.telaExcluir()
        Snackbar.make(
            view,
            "Confirma exclusão item $item",
            Snackbar.LENGTH_LONG
        ).setAction(
            "CONFIRMAR"
        ) {
            LevantRecyclerViewAdapter.levantSelect?.let { it1 -> levantamentoViewModel.excluirItem(it1) }
        }.show()
        levantamentoViewModel.clearAll()
    }
}

