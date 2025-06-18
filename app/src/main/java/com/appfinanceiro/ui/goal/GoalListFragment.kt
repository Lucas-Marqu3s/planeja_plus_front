package com.appfinanceiro.ui.goal

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.appfinanceiro.R
import com.appfinanceiro.api.ApiClient
import com.appfinanceiro.databinding.FragmentGoalListBinding
import com.appfinanceiro.model.Goal
import com.appfinanceiro.model.GoalStatus
import com.appfinanceiro.model.GoalType
import com.appfinanceiro.model.Store
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
            val intent = Intent(requireContext(), GoalFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter(
            goals = emptyList(),
            onItemClick = { goal ->
                // Implementar ação de clique na meta
                // Navegar para detalhes ou edição da meta
            },
            onTipsClick = { goal ->
                showTipsModal(goal)
            }
        )

        binding.rvGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalAdapter
        }
    }

    private fun showTipsModal(goal: Goal) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val modalView = LayoutInflater.from(requireContext()).inflate(R.layout.modal_tips, null)
        dialog.setContentView(modalView)

        // Configurar tamanho do modal
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Atualizar conteúdo baseado no tipo e status da meta
        updateModalContent(modalView, goal)

        // Configurar botão de fechar
        modalView.findViewById<View>(R.id.btnCloseModal).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateModalContent(modalView: View, goal: Goal) {
        val titleView = modalView.findViewById<TextView>(R.id.tvModalTitle)
        val contentContainer = modalView.findViewById<LinearLayout>(R.id.llTipsContent)

        if (goal.status == GoalStatus.COMPLETED) {
            // 1. Guarda o nome da meta
            val goalName: String = goal.name

            // 2. Ajusta o título usando o nome
            titleView.text = "Lojas Recomendadas para $goalName"

            // 3. Limpa as views antigas
            contentContainer.removeAllViews()

            // 4. Converte o nome (displayName) de volta para GoalType
            //    (caso não encontre, resultará em null)
            val goalType: GoalType? = GoalType.values()
                .firstOrNull { it.displayName.equals(goalName, ignoreCase = true) }

            // 5. Busca as lojas correspondentes — ou lista vazia se não encontrou o tipo
            val stores: List<Store> = goalType
                ?.let { getStoresForGoalType(it) }
                ?: emptyList()

            // 6. Infla cada store no container
            stores.forEach { store ->
                val storeView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_store_simple, contentContainer, false)

                storeView.findViewById<TextView>(R.id.tvStoreName).text = store.name
                storeView.findViewById<TextView>(R.id.tvStoreDescription).text = store.description
                storeView.findViewById<TextView>(R.id.tvStoreRating).text = "⭐ ${store.rating}"

                storeView.setOnClickListener {
                    store.website?.let { url ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }

                contentContainer.addView(storeView)
            }
        } else {
            // Fluxo para metas não concluídas
            titleView.text = "Dicas para ${goal.name}"
            // mantém o layout de dicas padrão
        }

    }

    private fun getStoresForGoalType(goalType: GoalType?): List<Store> {
        return when (goalType) {
            GoalType.CARRO -> listOf(
                Store(1, "Concessionária Honda", "Carros novos e seminovos Honda com garantia", null, 4.5f, "https://honda.com.br", "Automóveis"),
                Store(2, "Toyota Motors", "Veículos Toyota com qualidade e confiabilidade", null, 4.7f, "https://toyota.com.br", "Automóveis"),
                Store(3, "Webmotors", "Maior marketplace de carros usados do Brasil", null, 4.2f, "https://webmotors.com.br", "Automóveis"),
                Store(4, "Chevrolet", "Carros Chevrolet novos e seminovos", null, 4.3f, "https://chevrolet.com.br", "Automóveis")
            )
            GoalType.CASA -> listOf(
                Store(5, "Lopes Imóveis", "Imóveis residenciais e comerciais em todo Brasil", null, 4.3f, "https://lopes.com.br", "Imóveis"),
                Store(6, "ZAP Imóveis", "Portal líder em imóveis no Brasil", null, 4.1f, "https://zapimoveis.com.br", "Imóveis"),
                Store(7, "Viva Real", "Compra, venda e aluguel de imóveis", null, 4.4f, "https://vivareal.com.br", "Imóveis"),
                Store(8, "Cyrela", "Construtora e incorporadora de imóveis", null, 4.2f, "https://cyrela.com.br", "Imóveis")
            )
            GoalType.INVESTIMENTO -> listOf(
                Store(9, "XP Investimentos", "Corretora líder em investimentos", null, 4.6f, "https://xpi.com.br", "Investimentos"),
                Store(10, "Rico Investimentos", "Plataforma completa de investimentos", null, 4.4f, "https://rico.com.vc", "Investimentos"),
                Store(11, "BTG Pactual", "Banco de investimentos", null, 4.5f, "https://btgpactual.com", "Investimentos"),
                Store(12, "Inter Invest", "Investimentos do Banco Inter", null, 4.3f, "https://inter.co", "Investimentos")
            )
            GoalType.ROUPA -> listOf(
                Store(13, "Zara", "Moda internacional com estilo", null, 4.2f, "https://zara.com", "Moda"),
                Store(14, "C&A", "Moda para toda família", null, 4.0f, "https://cea.com.br", "Moda"),
                Store(15, "Renner", "Moda e estilo brasileiro", null, 4.1f, "https://lojasrenner.com.br", "Moda"),
                Store(16, "Riachuelo", "Moda democrática e acessível", null, 3.9f, "https://riachuelo.com.br", "Moda")
            )
            GoalType.FERIAS -> listOf(
                Store(17, "Booking.com", "Reservas de hotéis em todo mundo", null, 4.6f, "https://booking.com", "Viagens"),
                Store(18, "Decolar", "Passagens aéreas e pacotes de viagem", null, 4.2f, "https://decolar.com", "Viagens"),
                Store(19, "CVC", "Agência de viagens com pacotes exclusivos", null, 4.1f, "https://cvc.com.br", "Viagens"),
                Store(20, "Latam Travel", "Viagens e experiências únicas", null, 4.3f, "https://latam.com", "Viagens")
            )
            GoalType.COMPUTADOR -> listOf(
                Store(21, "Kabum", "Loja especializada em tecnologia", null, 4.4f, "https://kabum.com.br", "Tecnologia"),
                Store(22, "Pichau", "Hardware e periféricos gamer", null, 4.3f, "https://pichau.com.br", "Tecnologia"),
                Store(23, "Dell", "Computadores e notebooks Dell", null, 4.5f, "https://dell.com.br", "Tecnologia"),
                Store(24, "Lenovo", "Notebooks e desktops Lenovo", null, 4.2f, "https://lenovo.com.br", "Tecnologia")
            )
            else -> emptyList()
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

