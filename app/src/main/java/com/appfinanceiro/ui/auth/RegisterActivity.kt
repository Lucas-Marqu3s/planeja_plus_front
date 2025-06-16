package com.appfinanceiro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appfinanceiro.databinding.ActivityRegisterBinding
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.model.RegisterRequest
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiClient = ApiClient(sessionManager)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                register(name, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Nome é obrigatório"
            binding.etName.requestFocus()
            return false
        }

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

        if (password.length < 6) {
            binding.etPassword.error = "Senha deve ter pelo menos 6 caracteres"
            binding.etPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Senhas não conferem"
            binding.etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun register(name: String, email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val registerRequest = RegisterRequest(name, email, password)
                val response = apiClient.apiService.register(registerRequest)

                if (response.isSuccessful) {
                    showSuccess("Cadastro realizado com sucesso! Faça login para continuar.")
                    // Navega para a LoginActivity
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    showError("Falha ao cadastrar: ${response.errorBody()?.string()}")
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
