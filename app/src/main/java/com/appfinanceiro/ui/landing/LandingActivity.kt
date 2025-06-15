package com.appfinanceiro.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appfinanceiro.MainActivity
import com.appfinanceiro.databinding.ActivityLandingBinding
import com.appfinanceiro.ui.auth.LoginActivity
import com.appfinanceiro.ui.auth.RegisterActivity
import com.appfinanceiro.ui.transaction.TransactionFormActivity
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Timer
import java.util.TimerTask

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Clique no botão de nova transação
        binding.btnNewTransaction.setOnClickListener {
            // Por exemplo, abre outra Activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Clique para continuar
        binding.tvContinue.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

//    private fun setupButtons() {
//        // Configurar botões de navegação
//        binding.btnNewTransaction.setOnClickListener {
//            startActivity(Intent(this, TransactionFormActivity::class.java))
//        }
//
//        binding.tvContinue.setOnClickListener {
//            // Navegar para o dashboard principal
//            startActivity(Intent(this, MainActivity::class.java))
//            finish() // Finalizar a LandingActivity para que o usuário não volte para ela ao pressionar "voltar"
//        }
//    }

}
