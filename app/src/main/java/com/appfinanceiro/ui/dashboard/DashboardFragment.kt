package com.appfinanceiro.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appfinanceiro.R
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.FragmentDashboardBinding
import com.appfinanceiro.model.Transaction
import com.appfinanceiro.model.TransactionType
import com.appfinanceiro.ui.transaction.TransactionAdapter
import com.appfinanceiro.ui.transaction.TransactionFormActivity
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private lateinit var transactionAdapter: TransactionAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient(sessionManager)
        
        setupRecyclerView()
        loadTransactions()

        binding.fabAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), TransactionFormActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            // Implementar ação de clique na transação
            // Navegar para detalhes ou edição da transação
        }
        
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }
    
    private fun loadTransactions() {
        lifecycleScope.launch {
            try {
                val response = apiClient.apiService.getAllTransactions()
                
                if (response.isSuccessful && response.body() != null) {
                    val transactions = response.body()!!
                    updateDashboard(transactions)
                    
                    // Mostrar apenas as transações mais recentes no dashboard
                    val recentTransactions = transactions.sortedByDescending { it.date }.take(5)
                    transactionAdapter.updateTransactions(recentTransactions)
                }
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }
    
    private fun updateDashboard(transactions: List<Transaction>) {
        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO
        
        transactions.forEach { transaction ->
            when (transaction.type) {
                TransactionType.INCOME -> totalIncome = totalIncome.add(transaction.amount)
                TransactionType.EXPENSE -> totalExpense = totalExpense.add(transaction.amount)
            }
        }
        
        val balance = totalIncome.subtract(totalExpense)
        
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        
        binding.tvBalance.text = currencyFormat.format(balance)
        binding.tvIncome.text = currencyFormat.format(totalIncome)
        binding.tvExpense.text = currencyFormat.format(totalExpense)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
