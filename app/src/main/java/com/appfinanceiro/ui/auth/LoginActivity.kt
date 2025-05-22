package com.appfinanceiro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appfinanceiro.MainActivity
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.ActivityLoginBinding
import com.appfinanceiro.model.LoginRequest
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiClient = ApiClient(sessionManager)

        // Verificar se o usuário já está logado
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email é obrigatório"
            binding.etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Senha é obrigatória"
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun login(email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = apiClient.apiService.login(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    // Salvar dados da sessão
                    sessionManager.saveAuthToken(authResponse.accessToken)
                    sessionManager.saveUserInfo(
                        authResponse.userId,
                        authResponse.name,
                        authResponse.email
                    )
                    
                    navigateToMainActivity()
                } else {
                    showError("Falha ao fazer login. Verifique suas credenciais.")
                }
            } catch (e: HttpException) {
                showError("Erro de conexão: ${e.message()}")
            } catch (e: IOException) {
                showError("Erro de rede: Verifique sua conexão com a internet")
            } catch (e: Exception) {
                showError("Erro desconhecido: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
