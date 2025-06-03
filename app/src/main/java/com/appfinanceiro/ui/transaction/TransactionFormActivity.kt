package com.appfinanceiro.ui.transaction

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appfinanceiro.MainActivity
import com.appfinanceiro.R // Make sure R is imported correctly
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.api.ApiService
import com.appfinanceiro.databinding.ActivityTransactionFormBinding
import com.appfinanceiro.model.TransactionRequest
import com.appfinanceiro.model.TransactionType // Assuming TransactionType enum exists
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.math.BigDecimal

class TransactionFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionFormBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiService = ApiClient(sessionManager).apiService

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Mostra a seta
        //supportActionBar?.setDisplayShowHomeEnabled(true) // Habilita a navegação
        supportActionBar?.title = "Adicionar Transação"

        setupCategoryDropdown()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupCategoryDropdown() {
        // Replace with actual categories fetched from API or defined elsewhere
        val categories = arrayOf("Salário", "Alimentação", "Transporte", "Lazer", "Moradia", "Outros")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this@TransactionFormActivity,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        // Set initial date
        updateDateInView()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(calendar.time))
    }

    private fun setupSaveButton() {
        binding.btnSaveTransaction.setOnClickListener {
            val description = binding.etDescription.text.toString().trim()
            val valueStr = binding.etValue.text.toString().trim()
            val category = binding.actvCategory.text.toString().trim()
            val date: Date = calendar.time // Use the selected Date object
            val transactionType = if (binding.rbIncome.isChecked) TransactionType.INCOME else TransactionType.EXPENSE

            if (description.isEmpty() || valueStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val value = try {
                BigDecimal(valueStr)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transactionRequest = TransactionRequest(
                type = transactionType,
                description = description,
                amount = value,
                category = category,
                date = date
            )

            saveTransaction(transactionRequest)
        }
    }

    private fun saveTransaction(transactionRequest: TransactionRequest) {
        lifecycleScope.launch {
            try {
                val response = apiService.createTransaction(transactionRequest)
                if (response.isSuccessful) {
                    Toast.makeText(this@TransactionFormActivity, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show()
                    // Navigate back to MainActivity
                    val intent = Intent(this@TransactionFormActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish() // Close the form activity
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                    Log.e("TransactionForm", "Erro ao salvar transação: ${response.code()} - $errorBody")
                    Toast.makeText(this@TransactionFormActivity, "Erro ao salvar: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("TransactionForm", "Exceção ao salvar transação", e)
                Toast.makeText(this@TransactionFormActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Volta para a tela anterior
        return true
    }

}