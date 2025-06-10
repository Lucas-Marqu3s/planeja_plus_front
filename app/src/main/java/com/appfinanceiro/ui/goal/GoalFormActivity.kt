package com.appfinanceiro.ui.goal

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
import com.appfinanceiro.databinding.ActivityGoalFormBinding
import com.appfinanceiro.model.GoalRequest
import com.appfinanceiro.model.GoalStatus // Assuming GoalStatus enum exists in model package
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.math.BigDecimal

class GoalFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalFormBinding
    private val calendar: Calendar = Calendar.getInstance()
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService
    private var selectedDeadline: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        apiService = ApiClient(sessionManager).apiService

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Mostra a seta
        supportActionBar?.title = "Adicionar Meta"

        setupStatusSpinner()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupStatusSpinner() {
        // Get status names from enum or strings resource
        // Ensure these strings are defined in strings.xml
        val statusOptions = GoalStatus.values().map {
            when(it) {
                GoalStatus.IN_PROGRESS -> getString(R.string.status_in_progress)
                GoalStatus.COMPLETED -> getString(R.string.status_completed)
                GoalStatus.CANCELED -> getString(R.string.status_canceled)
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGoalStatus.adapter = adapter
        // Set default selection if needed, e.g., IN_PROGRESS
        binding.spinnerGoalStatus.setSelection(GoalStatus.IN_PROGRESS.ordinal)
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDeadline = calendar.time // Store the selected date
            updateDeadlineInView()
        }

        binding.etDeadline.setOnClickListener {
            DatePickerDialog(
                this@GoalFormActivity,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDeadlineInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDeadline.setText(sdf.format(calendar.time))
    }

    private fun setupSaveButton() {
        binding.btnSaveGoal.setOnClickListener {
            val name = binding.etGoalName.text.toString().trim()
            val targetAmountStr = binding.etTargetAmount.text.toString().trim()
            val currentAmountStr = binding.etCurrentAmount.text.toString().trim()
            val description = binding.etGoalDescription.text.toString().trim()
            val selectedStatusPosition = binding.spinnerGoalStatus.selectedItemPosition

            if (name.isEmpty() || targetAmountStr.isEmpty() || currentAmountStr.isEmpty()) {
                Toast.makeText(this, "Nome, valor alvo e valor atual são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val targetAmount = try {
                BigDecimal(targetAmountStr)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Valor alvo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentAmount = try {
                BigDecimal(currentAmountStr)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Valor atual inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentAmount > targetAmount) {
                Toast.makeText(this, "Valor atual não pode ser maior que o valor alvo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val status = GoalStatus.values()[selectedStatusPosition]

            val goalRequest = GoalRequest(
                name = name,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                deadline = selectedDeadline, // Use the selected Date object or null
                description = description.ifEmpty { null }, // Send null if description is empty
                status = status
            )

            saveGoal(goalRequest)
        }
    }

    private fun saveGoal(goalRequest: GoalRequest) {
        lifecycleScope.launch {
            try {
                val response = apiService.createGoal(goalRequest) // Assuming createGoal endpoint exists
                if (response.isSuccessful) {
                    Toast.makeText(this@GoalFormActivity, "Meta salva com sucesso!", Toast.LENGTH_SHORT).show()
                    // Navigate back to GoalList
                    val intent = Intent(this@GoalFormActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("navigateTo", "goalList")
                    startActivity(intent)
                    finish()

                } else {
                    val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                    Log.e("GoalForm", "Erro ao salvar meta: ${response.code()} - $errorBody")
                    Toast.makeText(this@GoalFormActivity, "Erro ao salvar: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("GoalForm", "Exceção ao salvar meta", e)
                Toast.makeText(this@GoalFormActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Volta para a tela anterior
        return true
    }
}

