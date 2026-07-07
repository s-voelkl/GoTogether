package svoelkl2.mauc.androidtest01.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import svoelkl2.mauc.androidtest01.MainViewModel
import svoelkl2.mauc.androidtest01.R
import svoelkl2.mauc.androidtest01.ui.quests.Quest

class MapFragment : Fragment() {
    
    private val viewModel: MainViewModel by activityViewModels()
    private var _map: MapView? = null
    private val map get() = _map!!
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ctx = requireContext().applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = requireContext().packageName

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        
        _map = root.findViewById(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        
        val mapController = map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(49.731694, 12.193381) // Near Weiden
        mapController.setCenter(startPoint)

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        // Handle current location click: move to own location
        locationOverlay.runOnFirstFix {
            activity?.runOnUiThread {
                if (_map != null) {
                    map.controller.animateTo(locationOverlay.myLocation)
                }
            }
        }
        map.overlays.add(locationOverlay)

        root.findViewById<FloatingActionButton>(R.id.fab_my_location).setOnClickListener {
            val myLoc = locationOverlay.myLocation
            if (myLoc != null) {
                map.controller.animateTo(myLoc)
            } else {
                // If no fix yet, just center on Munich or something
                map.controller.animateTo(GeoPoint(48.1351, 11.5820))
            }
        }

        viewModel.quests.observe(viewLifecycleOwner) { quests ->
            map.overlays.filterIsInstance<Marker>().forEach { map.overlays.remove(it) }
            quests.forEach { addQuestMarker(it) }
            map.invalidate()
        }

        // Handle direct focus from Quest Detail
        viewModel.selectedQuest.observe(viewLifecycleOwner) { quest ->
            if (quest != null && viewModel.navToQuests.value == false) {
                // We use navToQuests=true for Map->Quests, so here we check if we came back?
                // Actually, let's just animate if selectedQuest changes and we are visible.
                val point = GeoPoint(quest.latitude, quest.longitude)
                map.controller.animateTo(point)
                map.controller.setZoom(17.0)
            }
        }

        return root
    }

    private fun addQuestMarker(quest: Quest) {
        val marker = Marker(map)
        marker.position = GeoPoint(quest.latitude, quest.longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = quest.title
        marker.snippet = if (quest.isDone) "[DONE] ${quest.type}" else "${quest.type} (${quest.duration})"
        
        // Color markers based on type or status
        val color = if (quest.isDone) {
            android.graphics.Color.GRAY
        } else {
            when (quest.type) {
                "Social" -> android.graphics.Color.BLUE
                "Professional" -> android.graphics.Color.RED
                "Food" -> android.graphics.Color.YELLOW
                "Sports" -> android.graphics.Color.GREEN
                else -> android.graphics.Color.CYAN
            }
        }
        
        val baseDrawable = androidx.core.content.res.ResourcesCompat.getDrawable(resources, org.osmdroid.library.R.drawable.marker_default, null)
        val icon = baseDrawable?.mutate()?.apply {
            setTint(color)
        }
        marker.icon = icon

        marker.setOnMarkerClickListener { _, _ ->
            viewModel.selectQuest(quest)
            findNavController().navigate(R.id.navigation_quests)
            true
        }

        map.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        _map?.onResume()
    }

    override fun onPause() {
        super.onPause()
        _map?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _map = null
    }
}