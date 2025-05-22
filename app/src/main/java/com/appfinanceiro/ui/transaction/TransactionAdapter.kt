package com.appfinanceiro.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appfinanceiro.databinding.ItemTransactionBinding
import com.appfinanceiro.model.Transaction
import com.appfinanceiro.model.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(transactions[position])
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.tvTransactionDescription.text = transaction.description
            binding.tvTransactionCategory.text = transaction.category
            binding.tvTransactionDate.text = dateFormat.format(transaction.date)

            val amount = currencyFormat.format(transaction.amount)
            binding.tvTransactionAmount.text = amount

            // Definir cor com base no tipo de transação
            val textColor = if (transaction.type == TransactionType.INCOME) {
                android.graphics.Color.parseColor("#4CAF50") // Verde
            } else {
                android.graphics.Color.parseColor("#F44336") // Vermelho
            }
            binding.tvTransactionAmount.setTextColor(textColor)
        }
    }
}
