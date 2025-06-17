package com.appfinanceiro.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.DialogTransactionDetailsBinding
import com.appfinanceiro.databinding.FragmentTransactionListBinding
import com.appfinanceiro.model.Transaction
import com.appfinanceiro.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactions = mutableListOf<Transaction>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))


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
            startActivity(Intent(requireContext(), TransactionFormActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(transactions) { tx ->
            showTransactionDialog(tx)
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
                    transactions.clear()
                    transactions.addAll(response.body()!!)
                    transactionAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Falha ao carregar transações", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTransactionDialog(tx: Transaction) {
        val dlgBinding = DialogTransactionDetailsBinding.inflate(layoutInflater)
        dlgBinding.dialogDesc.text = tx.description
        dlgBinding.dialogAmount.text = currencyFormat.format(tx.amount)
        dlgBinding.dialogCategory.text = tx.category
        dlgBinding.dialogDate.text = dateFormat.format(tx.date)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dlgBinding.root)
            .create()

        dlgBinding.btnDelete.setOnClickListener {
            dialog.dismiss()
            confirmDelete(tx)
        }

        dialog.show()
    }

    private fun confirmDelete(tx: Transaction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Excluir Transação")
            .setMessage("Tem certeza que deseja excluir “${tx.description}”?")
            .setPositiveButton("Sim") { d, _ ->
                d.dismiss()
                deleteTransaction(tx)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteTransaction(tx: Transaction) {
        lifecycleScope.launch {
            try {
                val resp = apiClient.apiService.deleteTransaction(tx.id!!)
                if (resp.isSuccessful) {
                    transactions.remove(tx)
                    transactionAdapter.notifyDataSetChanged()
                    Toast.makeText(context, "Transação excluída", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Falha ao excluir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
