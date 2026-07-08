package com.goTogether_android.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.goTogether_android.R
import com.goTogether_android.challenges.ChallengeDetailBottomSheet
import com.goTogether_android.data.*
import com.goTogether_android.util.MarkerGenerator
import com.goTogether_android.util.flyTo
import org.maplibre.android.annotations.Marker
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var map: MapLibreMap
    private lateinit var nearbyAdapter: NearbyAdapter
    private lateinit var dotsContainer: LinearLayout

    private val markerToChallengeId = mutableMapOf<Long, String>()
    private var selectedMarker: Marker? = null

    private var lastMarkerClickTime = 0L
    private var lastClickedMarkerId = -1L

    private val AMBERG_CENTER = LatLng(49.4422, 11.862)
    private val DEFAULT_ZOOM = 12.6
    private val CHALLENGE_ZOOM = 14.4

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_home, container, false)
        
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { m ->
            map = m
            setupMap()
            renderMarkers(mockChallenges)
        }

        dotsContainer = view.findViewById(R.id.dotsContainer)
        setupNearbyCarousel(view)

        handleArguments(arguments)

        view.findViewById<View>(R.id.headerRightSlot).setOnClickListener {
            FilterBottomSheet { filters ->
                // Apply filters logic
                val filtered = if (filters.isEmpty()) {
                    mockChallenges
                } else {
                    mockChallenges.filter { it.category in filters }
                }
                renderMarkers(filtered)
                nearbyAdapter.submitList(filtered)
                updateDots(filtered.size, 0)
            }.show(parentFragmentManager, "filter")
        }

        return view
    }

    private fun handleArguments(args: Bundle?) {
        val focusId = args?.getString("focusChallengeId")
        if (focusId != null) {
            mapView.getMapAsync { m ->
                val challenge = mockChallenges.find { it.id == focusId }
                if (challenge != null) {
                    focusChallenge(challenge)
                }
            }
        }
    }

    private fun setupMap() {
        map.setStyle(
            Style.Builder().fromUri("https://tiles.openfreemap.org/styles/positron")
        ) {
            val camera = CameraPosition.Builder()
                .target(AMBERG_CENTER)
                .zoom(DEFAULT_ZOOM)
                .build()

            map.animateCamera(CameraUpdateFactory.newCameraPosition(camera), 800)
        }
    }

    private fun renderMarkers(challenges: List<Challenge>) {
        if (!::map.isInitialized) return
        map.clear()
        markerToChallengeId.clear()
        selectedMarker = null

        challenges.forEach { challenge ->
            val cat = FILTER_CATEGORIES.find { it.id == challenge.category }
            val icon = MarkerGenerator.generate(requireContext(), cat?.color ?: "#6B7280", false)
            
            val marker = map.addMarker(
                org.maplibre.android.annotations.MarkerOptions()
                    .position(LatLng(challenge.lat, challenge.lng))
                    .title(challenge.name)
                    .icon(icon)
            )
            markerToChallengeId[marker.id] = challenge.id
        }

        map.setOnMarkerClickListener { marker ->
            val currentTime = System.currentTimeMillis()
            val id = markerToChallengeId[marker.id]
            val challenge = mockChallenges.find { it.id == id } ?: return@setOnMarkerClickListener true

            if (marker.id == lastClickedMarkerId && currentTime - lastMarkerClickTime < 350) {
                // Double Click
                ChallengeDetailBottomSheet.newInstance(challenge.id).show(parentFragmentManager, "detail")
            } else {
                // Single Click
                focusChallenge(challenge)
                
                val index = nearbyAdapter.items.indexOf(challenge)
                if (index >= 0) {
                    view?.findViewById<RecyclerView>(R.id.nearbyRecycler)?.smoothScrollToPosition(index)
                }
            }

            lastMarkerClickTime = currentTime
            lastClickedMarkerId = marker.id
            true
        }
    }

    private fun focusChallenge(challenge: Challenge) {
        map.flyTo(challenge.lat - 0.0015, challenge.lng, CHALLENGE_ZOOM, 650)
        
        // Find marker for this challenge and highlight it
        val markerEntry = markerToChallengeId.entries.find { it.value == challenge.id }
        val marker = map.markers.find { it.id == markerEntry?.key }
        
        if (marker != null && marker != selectedMarker) {
            // Deselect old
            selectedMarker?.let { prev ->
                val prevChallenge = mockChallenges.find { it.id == markerToChallengeId[prev.id] }
                val prevCat = FILTER_CATEGORIES.find { it.id == prevChallenge?.category }
                prev.icon = MarkerGenerator.generate(requireContext(), prevCat?.color ?: "#6B7280", false)
            }
            
            // Select new
            val cat = FILTER_CATEGORIES.find { it.id == challenge.category }
            marker.icon = MarkerGenerator.generate(requireContext(), cat?.color ?: "#6B7280", true)
            selectedMarker = marker
        }
    }

    private fun setupNearbyCarousel(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.nearbyRecycler)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recycler.layoutManager = layoutManager

        nearbyAdapter = NearbyAdapter { challenge ->
            focusChallenge(challenge)
        }

        recycler.adapter = nearbyAdapter
        nearbyAdapter.submitList(mockChallenges)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler)

        updateDots(mockChallenges.size, 0)

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(layoutManager)
                    if (centerView != null) {
                        val pos = layoutManager.getPosition(centerView)
                        updateDots(nearbyAdapter.itemCount, pos)
                        
                        val challenge = mockChallenges[pos]
                        focusChallenge(challenge)
                    }
                }
            }
        })
    }

    private fun updateDots(count: Int, activeIndex: Int) {
        dotsContainer.removeAllViews()
        if (count <= 1) return

        val density = resources.displayMetrics.density
        for (i in 0 until count) {
            val dot = View(requireContext())
            val params = LinearLayout.LayoutParams(
                if (i == activeIndex) (18 * density).toInt() else (7 * density).toInt(),
                (7 * density).toInt()
            )
            params.setMargins((4 * density).toInt(), 0, (4 * density).toInt(), 0)
            dot.layoutParams = params
            dot.setBackgroundResource(if (i == activeIndex) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
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
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
