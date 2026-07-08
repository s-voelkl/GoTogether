package com.goTogether_android.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goTogether_android.R
import com.goTogether_android.data.Challenge
import com.goTogether_android.data.ChallengeRepository
import com.goTogether_android.home.FilterBottomSheet

class ChallengesFragment : Fragment() {

    private lateinit var adapter: ChallengesAdapter
    private lateinit var emptyView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_challenges, container, false)
        
        emptyView = view.findViewById(R.id.emptyView)
        setupRecycler(view)
        setupListeners(view)
        
        return view
    }

    private fun setupRecycler(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.challengesRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = ChallengesAdapter { challenge ->
            openDetail(challenge)
        }
        
        recycler.adapter = adapter
        updateList(ChallengeRepository.mockChallenges)
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.headerRightSlot).setOnClickListener {
            FilterBottomSheet(listOf(), 100) { filters, minBattery ->
                val filtered = ChallengeRepository.mockChallenges.filter {
                    (filters.isEmpty() || it.category in filters) && it.minSocialBattery <= minBattery
                }
                updateList(filtered)
            }.show(parentFragmentManager, "filter")
        }

        view.findViewById<View>(R.id.scanButton).setOnClickListener {
            openCheckIn()
        }
    }

    private fun updateList(challenges: List<Challenge>) {
        adapter.submitList(challenges)
        emptyView.visibility = if (challenges.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openDetail(challenge: Challenge) {
        ChallengeDetailBottomSheet.newInstance(challenge.id)
            .show(parentFragmentManager, "detail")
    }

    private fun openCheckIn() {
        CheckInBottomSheet().show(parentFragmentManager, "checkin")
    }
}
