package com.goTogether_android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.goTogether_android.R
import com.goTogether_android.data.*
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

/**
 * The main landing screen of the application.
 * Displays a map with available challenges and a carousel for quick navigation.
 */
class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var mapManager: HomeMapManager
    private lateinit var assistantManager: HomeAssistantManager
    
    private lateinit var nearbyAdapter: NearbyAdapter
    private lateinit var dotsContainer: LinearLayout
    
    private var currentCategories: List<String> = emptyList()
    private var currentMinBattery: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.activity_home, container, false)
        
        mapView = v.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        
        dotsContainer = v.findViewById(R.id.dotsContainer)
        
        setupManagers(v)
        setupNearbyCarousel(v)
        setupMap()
        
        handleArguments(arguments)
        
        return v
    }

    private fun setupManagers(rootView: View) {
        mapManager = HomeMapManager(mapView) { challenge ->
            focusChallenge(challenge)
        }
        
        val aiCard = rootView.findViewById<View>(R.id.aiGreetingCard)
        assistantManager = HomeAssistantManager(aiCard, parentFragmentManager)
    }

    private fun handleArguments(args: Bundle?) {
        val focusId = args?.getString("focus_challenge_id")
        if (focusId != null) {
            ChallengeRepository.findById(focusId)?.let {
                focusChallenge(it)
            }
        }
    }

    private fun setupMap() {
        mapView.getMapAsync { mapLibreMap ->
            // Instantiate Style.Builder() with parentheses and provide the callback lambda
            mapLibreMap.setStyle(Style.Builder().fromUri("https://tiles.openfreemap.org/styles/positron")) { style ->
                mapManager.setup(mapLibreMap)
                mapManager.renderMarkers(ChallengeRepository.mockChallenges)
            }
        }
    }

    private fun focusChallenge(challenge: Challenge) {
        mapManager.focusChallenge(challenge)
        
        // Sync carousel
        val pos = ChallengeRepository.mockChallenges.indexOfFirst { it.id == challenge.id }
        if (pos != -1) {
            val rv = view?.findViewById<RecyclerView>(R.id.nearbyRecycler)
            rv?.smoothScrollToPosition(pos)
        }
    }

    private fun setupNearbyCarousel(v: View) {
        val rv = v.findViewById<RecyclerView>(R.id.nearbyRecycler)
        nearbyAdapter = NearbyAdapter { challenge ->
            focusChallenge(challenge)
        }
        
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = nearbyAdapter
        
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv)
        
        nearbyAdapter.submitList(ChallengeRepository.mockChallenges)
        updateDots(ChallengeRepository.mockChallenges.size, 0)

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(rv.layoutManager)
                    val pos = centerView?.let { rv.layoutManager?.getPosition(it) } ?: -1
                    if (pos != -1) {
                        updateDots(ChallengeRepository.mockChallenges.size, pos)
                        val challenge = ChallengeRepository.mockChallenges[pos]
                        mapManager.focusChallenge(challenge)
                    }
                }
            }
        })
    }

    private fun updateDots(count: Int, selectedIndex: Int) {
        dotsContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = View(context)
            val size = (8 * resources.displayMetrics.density).toInt()
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            dot.setBackgroundResource(
                if (i == selectedIndex) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
            )
            dotsContainer.addView(dot)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        assistantManager.setupAiGreeting()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        assistantManager.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
