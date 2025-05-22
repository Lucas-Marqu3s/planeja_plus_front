package com.appfinanceiro.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.FragmentProfileBinding
import com.appfinanceiro.ui.auth.LoginActivity
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient(sessionManager)
        
        loadUserProfile()
        setupListeners()
    }
    
    private fun loadUserProfile() {
        // Preencher campos com dados da sessão
        binding.etName.setText(sessionManager.getUserName())
        binding.etEmail.setText(sessionManager.getUserEmail())
        
        // Carregar dados atualizados do servidor
        lifecycleScope.launch {
            try {
                showLoading(true)
                val response = apiClient.apiService.getCurrentUser()
                
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    binding.etName.setText(user.name)
                    binding.etEmail.setText(user.email)
                    
                    // Atualizar dados da sessão
                    sessionManager.saveUserInfo(
                        sessionManager.getUserId(),
                        user.name,
                        user.email
                    )
                }
            } catch (e: Exception) {
                showError("Erro ao carregar perfil: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
        
        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }
        
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun updateProfile() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.etName.error = "Nome é obrigatório"
            binding.etName.requestFocus()
            return
        }
        
        if (email.isEmpty()) {
            binding.etEmail.error = "Email é obrigatório"
            binding.etEmail.requestFocus()
            return
        }
        
        lifecycleScope.launch {
            try {
                showLoading(true)
                
                val userUpdateRequest = mapOf(
                    "name" to name,
                    "email" to email
                )
                
                val response = apiClient.apiService.updateUser(
                    sessionManager.getUserId(),
                    userUpdateRequest
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val updatedUser = response.body()!!
                    
                    // Atualizar dados da sessão
                    sessionManager.saveUserInfo(
                        sessionManager.getUserId(),
                        updatedUser.name,
                        updatedUser.email
                    )
                    
                    showSuccess("Perfil atualizado com sucesso!")
                } else {
                    showError("Falha ao atualizar perfil: ${response.errorBody()?.string()}")
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
    
    private fun changePassword() {
        val currentPassword = binding.etCurrentPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmNewPassword = binding.etConfirmNewPassword.text.toString()
        
        if (currentPassword.isEmpty()) {
            binding.etCurrentPassword.error = "Senha atual é obrigatória"
            binding.etCurrentPassword.requestFocus()
            return
        }
        
        if (newPassword.isEmpty()) {
            binding.etNewPassword.error = "Nova senha é obrigatória"
            binding.etNewPassword.requestFocus()
            return
        }
        
        if (newPassword.length < 6) {
            binding.etNewPassword.error = "Nova senha deve ter pelo menos 6 caracteres"
            binding.etNewPassword.requestFocus()
            return
        }
        
        if (newPassword != confirmNewPassword) {
            binding.etConfirmNewPassword.error = "Senhas não conferem"
            binding.etConfirmNewPassword.requestFocus()
            return
        }
        
        lifecycleScope.launch {
            try {
                showLoading(true)
                
                val passwordUpdateRequest = mapOf(
                    "currentPassword" to currentPassword,
                    "newPassword" to newPassword
                )
                
                val response = apiClient.apiService.updatePassword(
                    sessionManager.getUserId(),
                    passwordUpdateRequest
                )
                
                if (response.isSuccessful) {
                    showSuccess("Senha atualizada com sucesso!")
                    
                    // Limpar campos de senha
                    binding.etCurrentPassword.text?.clear()
                    binding.etNewPassword.text?.clear()
                    binding.etConfirmNewPassword.text?.clear()
                } else {
                    showError("Falha ao atualizar senha: ${response.errorBody()?.string()}")
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
    
    private fun logout() {
        sessionManager.clearSession()
        
        // Navegar para tela de login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnUpdateProfile.isEnabled = !isLoading
        binding.btnChangePassword.isEnabled = !isLoading
        binding.btnLogout.isEnabled = !isLoading
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
