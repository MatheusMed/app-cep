package com.mmdvs.aplications

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.mmdvs.aplications.api.EnderecoApi
import com.mmdvs.aplications.api.RetrofitHelper
import com.mmdvs.aplications.databinding.ActivityMainBinding
import com.mmdvs.aplications.models.EnderecoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.Response

import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val http by lazy {
        RetrofitHelper.retrofit
    }

    private var pararThread  = false

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        binding.load.visibility = View.GONE
//        binding.btnIr.setOnClickListener{
//            startActivity(
//                Intent(this,SecundActivity::class.java)
//            )
//        }

        binding.btnIniciar.setOnClickListener{
//

            CoroutineScope(Dispatchers.IO).launch {
                recuperarEndereco()
            }


        }

        binding.btnParar.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                binding.inputCep.text = null
                binding.infoCep.text = ""
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private  suspend fun recuperarEndereco(){

        var retorno: Response<EnderecoModel>? = null

        var cepDigitado = binding.inputCep.text.toString()



     try {
         val res =  http.create(EnderecoApi::class.java)
         retorno =  res.recuperarEndereco(cepDigitado = cepDigitado)
     } catch (e:Exception){
        e.printStackTrace()
         Log.i("info_endereco","error ao recupera $e")
     }

        if(retorno != null){
            if(retorno.isSuccessful){

                val endereco = retorno.body()

                withContext(Dispatchers.Main){
                    binding.infoCep.text = "Cep: ${endereco?.cep} Rua: ${endereco?.bairro} Localidade: ${endereco?.localidade} Ibge: ${endereco?.ibge}"
                }



                Log.i("info_endereco","Localidade ${endereco.toString()}")
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private suspend fun execultar(){
        repeat(15) {index ->
            Log.i("info","Indice $index")
            withContext(Dispatchers.Main){
                binding.btnIniciar.text = "Iniciou a execucao $index ${Thread.currentThread().name}"
                binding.btnIniciar.isEnabled = false
//                binding.load.visibility = View.VISIBLE
            }
            delay(1000L)
        }
    }

    private suspend fun dadosUsuario(){
        val usurio = RecuperarUsuario()
        Log.i("info","Usuario ${usurio.nome} T:${Thread.currentThread().name}")
        val postagem = recuperarPostagemPeloId(usurio.id)
        Log.i("info","Postagem ${postagem.size} T:${Thread.currentThread().name}")
    }

    private suspend fun recuperarPostagemPeloId( idUsuario: Int): List<String>{
        delay(2000)
        return listOf(
            "viagem para o nordeste",
            "Aprendendo Android"
        )
    }

    private suspend fun RecuperarUsuario(): Usuario {
        delay(2000)
        return Usuario(
            10,
            "Matheus de Medeiros"
        )
    }


    inner class MinhaThread : Thread(){
        override fun run() {
            super.run()
            repeat(10){indice ->
                if(pararThread) {
                    pararThread = false
                    return
                }
                Log.i("info_thread","Execultado $indice T:${currentThread().name}")
                runOnUiThread {
                    binding.btnIniciar.text = "Execultado $indice T: ${currentThread().name}"
                    binding.btnIniciar.isEnabled = false
//                    binding.load.visibility = View.VISIBLE
                    if(indice >= 9){
                        binding.btnIniciar.isEnabled = true
//                        binding.load.visibility = View.GONE
                        binding.btnIniciar.text = "Processo Completo"
                    }

                }
                sleep(1000)
            }
        }
    }
}