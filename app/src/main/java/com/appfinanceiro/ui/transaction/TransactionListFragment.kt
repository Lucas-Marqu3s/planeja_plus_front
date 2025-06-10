package com.appfinanceiro.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.FragmentTransactionListBinding
import com.appfinanceiro.model.Transaction
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private lateinit var transactionAdapter: TransactionAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
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
        
        binding.rvTransactions.apply {
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
                    transactionAdapter.updateTransactions(transactions)
                }
            } catch (e: Exception) {
                // Tratar erro
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
