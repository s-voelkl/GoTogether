package gotogether.frontend.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import gotogether.frontend.android.model.Challenge
import gotogether.frontend.android.ui.components.NeoCard
import gotogether.frontend.android.ui.theme.Black
import gotogether.frontend.android.ui.theme.BrandYellow
import gotogether.frontend.android.ui.theme.MapBackground
import gotogether.frontend.android.viewmodel.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * MapScreen provides a visual representation of nearby challenges.
 * Uses Osmdroid for map rendering and pins challenges on the map.
 */
@Composable
fun MapScreen(viewModel: MainViewModel) {
    val challenges by viewModel.challenges.collectAsState()
    val context = LocalContext.current

    // Configure Osmdroid
    remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
        true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "EXPLORE",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        NeoCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            backgroundColor = MapBackground,
            padding = 2
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(52.52, 13.40)) // Berlin default
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    
                    // Add markers for challenges
                    challenges.forEach { challenge ->
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(challenge.latitude, challenge.longitude)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = challenge.title
                        marker.snippet = challenge.description
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeoCard(backgroundColor = Color.White) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${challenges.size} Challenges found near you",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ChallengeMarker(title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .background(BrandYellow, MaterialTheme.shapes.small)
                .border(2.dp, Black, MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
        }
        // Pin Stem
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(Black)
        )
    }
}

@Composable
private fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    padding: Int = 16,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(backgroundColor, MaterialTheme.shapes.medium)
            .border(2.dp, Black, MaterialTheme.shapes.medium)
            .padding(padding.dp),
        content = content
    )
}
