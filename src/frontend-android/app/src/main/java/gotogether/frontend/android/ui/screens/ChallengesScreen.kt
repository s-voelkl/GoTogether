package gotogether.frontend.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gotogether.frontend.android.model.Challenge
import gotogether.frontend.android.ui.components.NeoButton
import gotogether.frontend.android.ui.components.NeoCard
import gotogether.frontend.android.ui.theme.Black
import gotogether.frontend.android.ui.theme.BrandBlue
import gotogether.frontend.android.ui.theme.BrandYellow
import gotogether.frontend.android.viewmodel.MainViewModel

@Composable
fun ChallengesScreen(viewModel: MainViewModel) {
    val challenges by viewModel.challenges.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedChallenge by remember { mutableStateOf<Challenge?>(null) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CHALLENGES",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Black)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(challenges) { challenge ->
                    ChallengeItem(challenge, onJoinClick = {
                        selectedChallenge = challenge
                        showJoinDialog = true
                    })
                }
            }
        }
    }

    if (showJoinDialog && selectedChallenge != null) {
        JoinChallengeDialog(
            challenge = selectedChallenge!!,
            onDismiss = { showJoinDialog = false },
            onConfirm = { code ->
                viewModel.participateInChallenge(selectedChallenge!!.id, code)
                showJoinDialog = false
            }
        )
    }
}

@Composable
fun ChallengeItem(challenge: Challenge, onJoinClick: () -> Unit) {
    NeoCard(
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = challenge.hostCompanyName,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
            
            Surface(
                color = BrandYellow,
                shape = MaterialTheme.shapes.small,
                border = androidx.compose.foundation.BorderStroke(2.dp, Black)
            ) {
                Text(
                    text = "${challenge.currency} GT",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = challenge.description,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${challenge.currentPlayers}/${if(challenge.maxPlayers == 0) "∞" else challenge.maxPlayers} Players",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            
            NeoButton(
                text = "JOIN",
                onClick = onJoinClick,
                modifier = Modifier.height(36.dp)
            )
        }
    }
}

@Composable
fun JoinChallengeDialog(
    challenge: Challenge,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "VERIFY PARTICIPATION",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text("Enter the 5-digit verification code provided at the location.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 5) code = it },
                    label = { Text("5-Digit Code") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
        },
        confirmButton = {
            NeoButton(
                text = "VERIFY",
                onClick = { onConfirm(code) },
                enabled = code.length == 5
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Black, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.medium
    )
}
