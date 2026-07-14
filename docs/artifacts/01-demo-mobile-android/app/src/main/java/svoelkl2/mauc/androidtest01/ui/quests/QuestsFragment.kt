package svoelkl2.mauc.androidtest01.ui.quests

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import svoelkl2.mauc.androidtest01.MainViewModel
import svoelkl2.mauc.androidtest01.R

class QuestsFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: QuestAdapter

    private lateinit var filterBattery: CheckBox
    private lateinit var filterDistance: CheckBox
    private lateinit var interestSearch: AutoCompleteTextView
    private lateinit var selectedInterestsChips: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_quests, container, false)
        
        filterBattery = root.findViewById(R.id.filterBattery)
        filterDistance = root.findViewById(R.id.filterDistance)
        interestSearch = root.findViewById(R.id.interestSearchAutoComplete)
        selectedInterestsChips = root.findViewById(R.id.selectedInterestsChipGroup)

        val recyclerView = root.findViewById<RecyclerView>(R.id.questsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        adapter = QuestAdapter(emptyList()) { quest ->
            showQuestDetail(quest)
        }
        recyclerView.adapter = adapter

        setupInterestSearch()

        viewModel.quests.observe(viewLifecycleOwner) { quests ->
            applyFilters(quests)
        }

        viewModel.interests.observe(viewLifecycleOwner) { interests ->
            updateInterestChips(interests)
            applyFilters(viewModel.quests.value ?: emptyList())
        }

        val filterListener = View.OnClickListener {
            applyFilters(viewModel.quests.value ?: emptyList())
        }
        filterBattery.setOnClickListener(filterListener)
        filterDistance.setOnClickListener(filterListener)

        // Handle navigation from Map
        viewModel.selectedQuest.observe(viewLifecycleOwner) { quest ->
            if (quest != null && viewModel.navToQuests.value == true) {
                showQuestDetail(quest)
                viewModel.clearNav()
            }
        }

        return root
    }

    private fun setupInterestSearch() {
        val allInterests = viewModel.allAvailableInterests
        val searchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, allInterests)
        interestSearch.setAdapter(searchAdapter)
        interestSearch.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            viewModel.toggleInterest(selected)
            interestSearch.setText("")
        }
    }

    private fun updateInterestChips(interests: List<String>) {
        selectedInterestsChips.removeAllViews()
        interests.forEach { interest ->
            val chip = Chip(requireContext())
            chip.text = interest
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                viewModel.toggleInterest(interest)
            }
            selectedInterestsChips.addView(chip)
        }
    }

    private fun applyFilters(allQuests: List<Quest>) {
        var filtered = allQuests
        
        if (filterBattery.isChecked) {
            val battery = viewModel.socialBattery.value ?: 50
            filtered = filtered.filter { it.minBattery <= battery }
        }
        
        // Automatic interest filtering based on selection
        val userInterests = viewModel.interests.value ?: emptyList()
        if (userInterests.isNotEmpty()) {
            filtered = filtered.filter { quest ->
                quest.tags.any { userInterests.contains(it) }
            }
        }

        adapter.updateQuests(filtered)
    }

    private fun showQuestDetail(quest: Quest) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quest_detail, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.detailTitle).text = quest.title
        dialogView.findViewById<TextView>(R.id.detailDescription).text = quest.description
        dialogView.findViewById<TextView>(R.id.detailRewards).text = "Rewards: ${quest.reward}"
        dialogView.findViewById<TextView>(R.id.detailDuration).text = "Duration: ${quest.duration}"
        dialogView.findViewById<TextView>(R.id.detailCoordinates).text = "Location: ${quest.latitude}, ${quest.longitude}"
        dialogView.findViewById<TextView>(R.id.detailParticipants).text = "Participants: ${quest.participants.joinToString(", ").ifEmpty { "None" }}"
        dialogView.findViewById<TextView>(R.id.detailInterests).text = "Interests: ${quest.tags.joinToString(", ")}"
        
        val statusView = dialogView.findViewById<TextView>(R.id.detailStatus)
        if (quest.isDone) {
            statusView.visibility = View.VISIBLE
            statusView.text = "Status: COMPLETED"
        }

        dialogView.findViewById<Button>(R.id.btnVerify).apply {
            isEnabled = !quest.isDone
            setOnClickListener {
                showVerificationDialog(quest)
                dialog.dismiss()
            }
        }

        dialogView.findViewById<Button>(R.id.btnShowOnMap).setOnClickListener {
            viewModel.clearNav()
            findNavController().navigate(R.id.navigation_map)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showVerificationDialog(quest: Quest) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_verify_quest, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val etCode = dialogView.findViewById<android.widget.EditText>(R.id.etVerifyCode)

        dialogView.findViewById<Button>(R.id.btnMockScan).setOnClickListener {
            etCode.setText("test")
            Toast.makeText(context, "QR Scanned: test", Toast.LENGTH_SHORT).show()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmVerify).setOnClickListener {
            val code = etCode.text.toString()
            if (code == "test") {
                viewModel.verifyQuest(quest.id)
                Toast.makeText(context, "Challenge Verified!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Invalid code. Use 'test'", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<Button>(R.id.btnCancelVerify).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}