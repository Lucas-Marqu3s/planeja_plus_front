package com.appfinanceiro.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.FragmentGoalListBinding
import com.appfinanceiro.util.SessionManager
import kotlinx.coroutines.launch

class GoalListFragment : Fragment() {

    private var _binding: FragmentGoalListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private lateinit var goalAdapter: GoalAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient(sessionManager)
        
        setupRecyclerView()
        loadGoals()
        
        binding.fabAddGoal.setOnClickListener {
            // Navegar para tela de adicionar meta
            // Implementar navegação para GoalFormFragment
        }
    }
    
    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter(emptyList()) { goal ->
            // Implementar ação de clique na meta
            // Navegar para detalhes ou edição da meta
        }
        
        binding.rvGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }
    }
    
    private fun loadGoals() {
        lifecycleScope.launch {
            try {
                val response = apiClient.apiService.getAllGoals()
                
                if (response.isSuccessful && response.body() != null) {
                    val goals = response.body()!!
                    goalAdapter.updateGoals(goals)
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
