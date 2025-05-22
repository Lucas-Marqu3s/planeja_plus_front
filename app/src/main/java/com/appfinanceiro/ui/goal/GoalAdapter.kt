package com.appfinanceiro.ui.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appfinanceiro.databinding.ItemGoalBinding
import com.appfinanceiro.model.Goal
import com.appfinanceiro.model.GoalStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class GoalAdapter(
    private var goals: List<Goal>,
    private val onItemClick: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount() = goals.size

    inner class GoalViewHolder(private val binding: ItemGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(goals[position])
                }
            }
        }

        fun bind(goal: Goal) {
            binding.tvGoalName.text = goal.name
            
            // Formatar valores monetários
            val currentAmount = currencyFormat.format(goal.currentAmount)
            val targetAmount = currencyFormat.format(goal.targetAmount)
            binding.tvGoalProgress.text = "$currentAmount / $targetAmount"
            
            // Calcular e definir progresso
            val progressPercentage = if (goal.targetAmount.toDouble() > 0) {
                (goal.currentAmount.toDouble() / goal.targetAmount.toDouble() * 100).toInt()
            } else {
                0
            }
            binding.progressBarGoal.progress = progressPercentage
            
            // Formatar data limite, se existir
            binding.tvGoalDeadline.text = if (goal.deadline != null) {
                "Prazo: ${dateFormat.format(goal.deadline)}"
            } else {
                "Sem prazo definido"
            }
            
            // Definir status
            binding.tvGoalStatus.text = when (goal.status) {
                GoalStatus.IN_PROGRESS -> "Em andamento"
                GoalStatus.COMPLETED -> "Concluída"
                GoalStatus.CANCELED -> "Cancelada"
            }

// E também
            val textColor = when (goal.status) {
                GoalStatus.IN_PROGRESS -> android.graphics.Color.parseColor("#2196F3") // Azul
                GoalStatus.COMPLETED -> android.graphics.Color.parseColor("#4CAF50") // Verde
                GoalStatus.CANCELED -> android.graphics.Color.parseColor("#F44336") // Vermelho
            }
            binding.tvGoalStatus.setTextColor(textColor)
        }
    }
}
