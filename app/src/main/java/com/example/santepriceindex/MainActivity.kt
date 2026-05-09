package com.example.santepriceindex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Professional Color Palette
val DeepSpace = Color(0xFF0F0F0F)
val CardGray = Color(0xFF1E1E1E)
val NeonAmber = Color(0xFFFFC107)
val SoftGreen = Color(0xFF4CAF50)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val myViewModel: SanteViewModel = viewModel()
            val vegetableList by myViewModel.items.collectAsState()

            var transportInput by remember { mutableStateOf("5") }
            var wasteInput by remember { mutableStateOf("10") }
            var isSlateMode by remember { mutableStateOf(false) }

            // --- ANIMATED TRANSITION BETWEEN VIEWS ---
            AnimatedContent(
                targetState = isSlateMode,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() with slideOutHorizontally { it } + fadeOut()
                    }
                }
            ) { targetSlateMode ->
                if (targetSlateMode) {
                    // --- CUSTOMER VIEW: The "Digital Slate" ---
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .clickable { isSlateMode = false }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(32.dp)
                        ) {
                            items(vegetableList) { vegetable ->
                                val transport = transportInput.toDoubleOrNull() ?: 0.0
                                val waste = (wasteInput.toDoubleOrNull() ?: 0.0) / 100.0
                                val finalPrice = PricingEngine.calculateRRP(vegetable.mandiPrice, transport, waste, 5.0)

                                Column(modifier = Modifier.padding(bottom = 48.dp)) {
                                    Text(
                                        text = vegetable.name.uppercase(),
                                        color = Color.DarkGray,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                    )
                                    Text(
                                        text = "₹${"%.0f".format(finalPrice)}",
                                        color = NeonAmber,
                                        fontSize = 110.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Divider(color = Color(0xFF333333), thickness = 1.dp)
                                }
                            }
                        }
                    }
                } else {
                    // --- VENDOR VIEW: Modern Dashboard ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepSpace)
                            .padding(20.dp)
                    ) {
                        Text(
                            "SANTE INDEX",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text("Inventory & Margin Manager", color = Color.Gray, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { isSlateMode = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber)
                        ) {
                            Text("LAUNCH DIGITAL SLATE", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardGray)
                                .padding(16.dp)
                        ) {
                            Text("Cost Variables", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(value = transportInput, label = "Transport (₹/kg)") { transportInput = it }
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(value = wasteInput, label = "Waste (%)") { wasteInput = it }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("LIVE PRICE FEED", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(vegetableList) { vegetable ->
                                val transport = transportInput.toDoubleOrNull() ?: 0.0
                                val waste = (wasteInput.toDoubleOrNull() ?: 0.0) / 100.0
                                val suggested = PricingEngine.calculateRRP(vegetable.mandiPrice, transport, waste, 5.0)

                                // PULSE ANIMATION FOR LIVE PRICES
                                val infiniteTransition = rememberInfiniteTransition()
                                val alpha by infiniteTransition.animateFloat(
                                    initialValue = 0.7f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardGray)
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(vegetable.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text("Mandi: ₹${vegetable.mandiPrice}", color = Color.Gray, fontSize = 12.sp)
                                    }
                                    Text(
                                        "₹${"%.1f".format(suggested)}",
                                        color = SoftGreen.copy(alpha = alpha), // Pulsing effect
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(value: String, label: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = NeonAmber,
            unfocusedBorderColor = Color(0xFF333333)
        )
    )
}
