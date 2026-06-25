package gotogether.frontend.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gotogether.frontend.android.ui.components.NeoButton
import gotogether.frontend.android.ui.components.NeoCard
import gotogether.frontend.android.ui.theme.Black
import gotogether.frontend.android.ui.theme.BrandYellow
import gotogether.frontend.android.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val topics by viewModel.topics.collectAsState()
    
    var socialBattery by remember { mutableFloatStateOf(80f) }
    
    // Sync with user state if available
    LaunchedEffect(user) {
        user?.let {
            socialBattery = it.socialBattery.toFloat()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Social Battery Section
        NeoCard(
            backgroundColor = BrandYellow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SOCIAL BATTERY",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "How much energy do you have for others right now?",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Slider(
                value = socialBattery,
                onValueChange = { socialBattery = it },
                onValueChangeFinished = {
                    viewModel.updateSocialBattery(socialBattery.toInt())
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Black,
                    activeTrackColor = Black,
                    inactiveTrackColor = Color.White.copy(alpha = 0.5f)
                )
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Low", fontWeight = FontWeight.Bold)
                Text("${socialBattery.toInt()}%", fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Full", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Section
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NeoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = Color.White
            ) {
                Text("CURRENCY", style = MaterialTheme.typography.labelMedium)
                Text(
                    "${user?.currency ?: 0} GT",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
            NeoCard(
                modifier = Modifier.weight(1f),
                backgroundColor = Color.White
            ) {
                Text("LEVEL", style = MaterialTheme.typography.labelMedium)
                Text(
                    "${user?.level ?: 1}",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Interests Section
        Text(
            text = "MY INTERESTS",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (topic in topics) {
                val isSelected = user?.interests?.contains(topic.id) == true
                AssistChip(
                    onClick = { viewModel.toggleInterest(topic.id) },
                    label = { Text(topic.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) BrandYellow else Color.White,
                        labelColor = Black
                    ),
                    border = BorderStroke(2.dp, Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        NeoButton(
            text = "SAVE PREFERENCES",
            onClick = { /* Preferences are updated on change in this implementation */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (user == null) {
            NeoButton(
                text = "LOG IN (DEMO)",
                onClick = { viewModel.loginDemoUser() },
                backgroundColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
