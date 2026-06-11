package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.components.GlassCard
import com.example.ui.components.HolographicGrid
import com.example.ui.theme.*
import com.example.viewmodel.NexusViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBg)
                ) { innerPadding ->
                    NexusMainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NexusMainScreen(
    modifier: Modifier = Modifier,
    viewModel: NexusViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val macroNotification by viewModel.macroNotification.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Clear macro notification toast automatically after 4 seconds
    LaunchedEffect(macroNotification) {
        if (macroNotification != null) {
            delay(4000)
            viewModel.clearNotification()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Holographic Cyber Ambient Grid Backdrop
        HolographicGrid()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Futuristic TOP Heads-Up Display HUD Hub
            NexusTopHUD(viewModel = viewModel)

            // Dynamic Main Window Area based on screen route
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        "core_hub" -> CoreHubModule(viewModel = viewModel)
                        "ide" -> CodeEditorModule(viewModel = viewModel)
                        "automator" -> MacroAutomatorModule(viewModel = viewModel)
                        "blueprint" -> TechnicalManualModule()
                        else -> CoreHubModule(viewModel = viewModel)
                    }
                }
            }

            // High-Tech Bottom Dashboard Navigation Rail Bar
            NexusBottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { viewModel.navigateTo(it) }
            )
        }

        // Animated Macro Execution Alert Overlay
        macroNotification?.let { text ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonGreen.copy(alpha = 0.95f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notification alert",
                        tint = DarkBg,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = DarkBg,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun NexusTopHUD(viewModel: NexusViewModel) {
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val audioTrackProgress by viewModel.audioTrackProgress.collectAsState()

    var pulseValue by remember { mutableFloatStateOf(1f) }
    val infiniteTransition = rememberInfiniteTransition(label = "hudPulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hudPulseScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Bottom divider line with primary neon cyan glow
                drawLine(
                    color = NeonCyan,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
            },
        color = DarkSurface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nexus Terminal Identity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isAudioPlaying) NeonPink else NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEXUS_OS // OMNI-CORE v1.0",
                        color = NeonCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                }

                // Node Connection speed metrics
                Text(
                    text = "LNK_LAT: 12ms // STABLE",
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Integrated Space Lo-fi Audio deck controller card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.5.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Audio Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isAudioPlaying) NeonPink.copy(alpha = 0.15f) else NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, if (isAudioPlaying) NeonPink else NeonCyan, CircleShape)
                            .clickable { viewModel.toggleAudio() }
                            .testTag("audio_play_button")
                            .minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAudioPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = "Lo-Fi Music Deck",
                            tint = if (isAudioPlaying) NeonPink else NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DECK: " + if (isAudioPlaying) currentTrack else "AUDIO STREAMS STANDBY",
                                color = if (isAudioPlaying) NeonPink else CyberGrayText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                            if (isAudioPlaying) {
                                Text(
                                    text = "LIVE RIPPLE",
                                    color = NeonGreen,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(3.dp))
                        
                        // Fake progress slider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CyberGray)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(audioTrackProgress)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(NeonCyan, NeonPurple)
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- CORE HUB CONTAINER: PERSONAL AI + WALLET ledger ---
@Composable
fun CoreHubModule(viewModel: NexusViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val fiatBalance by viewModel.fiatBalance.collectAsState()
    val cryptoBalance by viewModel.cryptoBalance.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()

    var showWalletSection by remember { mutableStateOf(false) }
    var chatScrollState = rememberScrollState()

    val currentExpenses = remember(expenses) { expenses }

    // LazyColumn inside core layout
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Futuristic Dashboard Summary Card
        item {
            GlassCard(
                borderColor = NeonCyan,
                modifier = Modifier.testTag("dashboard_welcome_card")
            ) {
                Text(
                    text = "SYS_STATUS: ACTIVE // INTERNET PROTOCOL",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome to your Core Node",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "OmniCore coordinates advanced terminal coding files, monitors financial crypto transaction ledgers, registers automated triggers, and guides home device switches instantly.",
                    color = CyberGrayText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Switch Button to toggle AI Chat vs Financial Ledger Wallet View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showWalletSection = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showWalletSection) NeonCyan else CyberGray.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_ai_button")
                            .minimumInteractiveComponentSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Neural AI Link", 
                            color = if (!showWalletSection) DarkBg else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = { showWalletSection = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showWalletSection) NeonCyan else CyberGray.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_wallet_button")
                            .minimumInteractiveComponentSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Financial Node", 
                            color = if (showWalletSection) DarkBg else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        if (!showWalletSection) {
            // MODULE: AI COMPANION LINK (Gemini-powered chat)
            item {
                Text(
                    text = ">> OMNICORE NEURAL CHAT ASSISTANT",
                    color = NeonPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Scrollable chat body
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                chatMessages.forEach { msg ->
                                    ChatBubbleItem(msg)
                                }
                                if (isAiTyping) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(NeonCyan)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "OmniCore compiling query...",
                                            color = NeonCyan,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        // Smart Keyboard shortcut chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val shortcuts = listOf("review budget", "sample python code", "active macros", "add expense transport taxi 20")
                            shortcuts.forEach { phrase ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CyberGray)
                                        .border(0.5.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .clickable { viewModel.sendMessage(phrase) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = phrase,
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // User message direct send console line
                        var promptText by remember { mutableStateOf("") }
                        val keyboardController = LocalSoftwareKeyboardController.current

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = promptText,
                                onValueChange = { promptText = it },
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                ),
                                cursorBrush = SolidColor(NeonCyan),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Send
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (promptText.trim().isNotEmpty()) {
                                            viewModel.sendMessage(promptText)
                                            promptText = ""
                                            keyboardController?.hide()
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, CyberGray, RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("chat_input_text")
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonCyan)
                                    .clickable {
                                        if (promptText.trim().isNotEmpty()) {
                                            viewModel.sendMessage(promptText)
                                            promptText = ""
                                            keyboardController?.hide()
                                        }
                                    }
                                    .testTag("send_chat_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = DarkBg,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // MODULE: SMART CRYPTO WALLET & LEDGER SYSTEM
            item {
                Text(
                    text = ">> QUANTUM FINANCIAL PORTFOLIO STATUS",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            // Ledger Balances Holographic Box
            item {
                GlassCard(borderColor = NeonPurple) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "TOTAL FIAT LIQUIDITY",
                                color = CyberGrayText,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "$${String.format("%.2f", fiatBalance)}",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "CRYPTO WALLET VALUE",
                                color = NeonPurple,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "$${String.format("%.2f", cryptoBalance)}",
                                color = NeonCyan,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "BTC Balance: 0.12 BTC | ETH Balance: 1.4 ETH",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            item {
                Text(
                    text = ">> INSERT NEW ENTRY",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            // Interactive Transaction Insertion Card
            item {
                var descInput by remember { mutableStateOf("") }
                var amountInput by remember { mutableStateOf("") }
                var categoryInput by remember { mutableStateOf("FOOD") }
                var isCryptoSelected by remember { mutableStateOf(false) }

                GlassCard(borderColor = NeonPink) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BasicTextField(
                                value = descInput,
                                onValueChange = { descInput = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .border(1.dp, CyberGray, RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.2f))
                                    .padding(8.dp)
                                    .testTag("expense_desc_input"),
                                decorationBox = { innerTextField ->
                                    if (descInput.isEmpty()) Text("Details e.g. server gas", color = CyberGrayText, fontSize = 11.sp)
                                    innerTextField()
                                }
                            )

                            BasicTextField(
                                value = amountInput,
                                onValueChange = { amountInput = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, CyberGray, RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.2f))
                                    .padding(8.dp)
                                    .testTag("expense_amount_input"),
                                decorationBox = { innerTextField ->
                                    if (amountInput.isEmpty()) Text("$$ Amt", color = CyberGrayText, fontSize = 11.sp)
                                    innerTextField()
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category picker selections
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val categories = listOf("FOOD", "SERVER", "CRYPTO")
                                categories.forEach { cat ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (categoryInput == cat) NeonPurple else CyberGray)
                                            .clickable { 
                                                categoryInput = cat
                                                if (cat == "CRYPTO") isCryptoSelected = true
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(cat, color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            // Dynamic Save Button
                            Button(
                                onClick = {
                                    val amtValue = amountInput.toDoubleOrNull()
                                    if (descInput.isNotEmpty() && amtValue != null) {
                                        viewModel.addExpenseDirectly(descInput, amtValue, categoryInput, isCryptoSelected)
                                        descInput = ""
                                        amountInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .testTag("add_expense_btn")
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text("Post Ledger", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = ">> RECENT SECURE TRANSACTION LINES",
                    color = NeonPink,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            // Ledger Lines List
            items(currentExpenses) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .border(0.5.dp, CyberGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.description,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Category: ${item.category} // Db Ref: Room",
                                color = CyberGrayText,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "-$${String.format("%.2f", item.amount)}",
                            color = if (item.isCrypto) NeonPink else NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(msg: ChatMessage) {
    val isNexus = msg.sender == "NEXUS_AI"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isNexus) Alignment.Start else Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = if (isNexus) Arrangement.Start else Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isNexus) 0.dp else 12.dp,
                            bottomEnd = if (isNexus) 12.dp else 0.dp
                        )
                    )
                    .background(if (isNexus) CyberGray else NeonCyan)
                    .border(
                        0.5.dp, 
                        if (isNexus) NeonPurple.copy(alpha = 0.5f) else NeonCyan, 
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isNexus) 0.dp else 12.dp,
                            bottomEnd = if (isNexus) 12.dp else 0.dp
                        )
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = msg.text,
                    color = if (isNexus) Color.White else DarkBg,
                    fontSize = 12.sp,
                    fontFamily = if (isNexus) FontFamily.Default else FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }
        Text(
            text = if (isNexus) "OMNICORE AI // SECURE" else "SYS AGENT // LOCAL",
            color = CyberGrayText,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// --- MODULE: FUTURISTIC MOBILE IDE SCREEN ---
@Composable
fun CodeEditorModule(viewModel: NexusViewModel) {
    val code by viewModel.codeEditorContent.collectAsState()
    val language by viewModel.codeLanguage.collectAsState()
    val logs by viewModel.terminalLogs.collectAsState()
    val isCompiling by viewModel.isCompiling.collectAsState()
    val isCopilotEnabled by viewModel.isCopilotEnabled.collectAsState()
    val copilotSuggestion by viewModel.copilotSuggestion.collectAsState()

    val lines = remember(code) { code.split("\n") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High-Tech Bar for languages selection
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val languages = listOf("Python", "JavaScript", "HTML/CSS", "C++")
                languages.forEach { lang ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (language == lang) NeonCyan else CyberGray)
                            .border(1.dp, if (language == lang) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setLanguage(lang) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("lang_tab_$lang")
                    ) {
                        Text(
                            text = lang,
                            color = if (language == lang) DarkBg else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Live Code Editor Canvas Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF02020A))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "omni_editor_${language.lowercase()}.bin",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Autocomplete toggle switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Copilot Overlay",
                            color = CyberGrayText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isCopilotEnabled,
                            onCheckedChange = { viewModel.toggleCopilot(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = NeonCyan.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.scale(0.7f).testTag("copilot_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Custom code text area + line numbers gutter on left side
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // Line numbers gutter
                    Column(
                        modifier = Modifier
                            .width(28.dp)
                            .fillMaxHeight()
                            .drawBehind {
                                drawLine(
                                    color = CyberGray,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 2f
                                )
                            }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        (1..maxOf(8, lines.size)).forEach { index ->
                            Text(
                                text = "$index",
                                color = CyberGrayText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(end = 4.dp, bottom = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Editable main code typing canvas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        BasicTextField(
                            value = code,
                            onValueChange = { viewModel.updateCode(it) },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            cursorBrush = SolidColor(NeonCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .testTag("code_text_field")
                        )
                    }
                }

                // AI keyboard integrated overlay copilot bubble
                copilotSuggestion?.let { suggestion ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonPurple.copy(alpha = 0.15f))
                            .border(0.5.dp, NeonPurple, RoundedCornerShape(6.dp))
                            .clickable { viewModel.updateCode(code + suggestion) }
                            .padding(8.dp)
                            .testTag("copilot_suggestion")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "AI copilot",
                                tint = NeonPurple,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TAP TO AUTO-COMPLETE COPILOT: \n$suggestion",
                                color = NeonPurple,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Toolbar: Holographic Run Sandbox and Clear logs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.executeCode() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    enabled = !isCompiling,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("run_holographic_btn")
                        .minimumInteractiveComponentSize()
                ) {
                    if (isCompiling) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DarkBg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("COMPILING SECURE VM...", color = DarkBg, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", tint = DarkBg)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RUN HOLOGRAPHIC SANDBOX", color = DarkBg, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    }
                }

                Button(
                    onClick = { viewModel.clearTerminal() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGray),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("clear_terminal_btn")
                        .minimumInteractiveComponentSize()
                ) {
                    Text("CLEAR TERMINAL", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // RETRO SECURE TERMINAL TITLE
        item {
            Text(
                text = ">> SECURE CONTAINER TERMINAL STDOUT",
                color = NeonCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        // Retro stdout monitor
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.5.dp, CyberGray, RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(10.dp)
            ) {
                // Scrollable terminal content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { logLine ->
                        Text(
                            text = logLine,
                            color = if (logLine.startsWith(">") || logLine.contains("SUCCESS")) NeonCyan else if (logLine.contains("Error") || logLine.contains("SANDBOX")) NeonPink else NeonGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// --- MODULE: AUTOMATOR MACROS & IOT SWITCH BOARD GRID ---
@Composable
fun MacroAutomatorModule(viewModel: NexusViewModel) {
    val macros by viewModel.macros.collectAsState()
    val iotDevices by viewModel.iotDevices.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = ">> PHONE MACROS TRIGGERS (INTELLI-RULES)",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Map macro rules layout
        items(macros) { rule ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(2.dp, if (rule.isActive) NeonCyan else CyberGray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rule.triggerName,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = rule.actionName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Status: " + if (rule.isActive) "LINKED FOR RECON" else "PAUSED SYNC",
                            color = if (rule.isActive) NeonGreen else CyberGrayText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Switch(
                        checked = rule.isActive,
                        onCheckedChange = { viewModel.toggleMacro(rule.id) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = NeonCyan.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("toggle_macro_${rule.id}")
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ">> OMNI-HOME IOT SMART CONTROLLERS",
                color = NeonPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Map IoT grid cards
        items(iotDevices) { device ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, if (device.isOn) NeonPurple else CyberGray, RoundedCornerShape(12.dp))
                    .clickable { viewModel.toggleIoT(device.name) }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = device.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Zone: ${device.room} // Mode: ${device.status}",
                            color = CyberGrayText,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (device.isOn) NeonPurple.copy(alpha = 0.2f) else CyberGray)
                            .border(1.dp, if (device.isOn) NeonPurple else CyberGrayText, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (device.isOn) NeonPurple else CyberGrayText)
                        )
                    }
                }
            }
        }
    }
}

// --- TECHNICAL BLUEPRINTS MANUAL MODULE ---
@Composable
fun TechnicalManualModule() {
    var selectedManualTab by remember { mutableStateOf("architecture") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf("architecture", "roadmap", "wireframe")
            tabs.forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedManualTab == tab) NeonPink else CyberGray)
                        .clickable { selectedManualTab = tab }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.uppercase(),
                        color = if (selectedManualTab == tab) DarkBg else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedManualTab) {
                "architecture" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("NEXUS_OS MODULAR PLAN // ARCH", color = NeonPink, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text("NexusOS uses an advanced architectural structure composed of isolated dynamic modules allowing crash containment and low-latency rendering execution.\n", color = Color.White, fontSize = 12.sp)
                        
                        Text("1. PACKAGE DIRECTORIES SPECIFICATION", color = NeonCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("""
                            • com.example.data: Models & local persistent Room Database definition.
                            • com.example.api: Retrofit network layers targeting Gemini AI rest protocols.
                            • com.example.viewmodel: Unified state orchestration streams (StateFlow pipelines).
                            • com.example.ui.components: Glassmorphism and Neon drawing primitives.
                            • com.example.ui.theme: Dark color schemes, sizing, typography.
                        """.trimIndent(), color = CyberGrayText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("2. DEPENDENCIES & LIBRARIES", color = NeonCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("""
                            • Room persistence: Fast SQL nodes for automated task variables.
                            • Retrofit + Moshi: Low latency query streaming backends.
                            • Jetpack Compose: Modern declarative layouts with hardware acceleration.
                        """.trimIndent(), color = CyberGrayText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                "roadmap" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("5-SPRINT MASTER ROADMAP // OMNI-CORE", color = NeonPink, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        
                        val sprints = listOf(
                            "Sprint 1" to "Core Framework Setup: Implement M3 design structure, navigation hub rails, and holographic grids.",
                            "Sprint 2" to "OmniEditor Code IDE: Complete Sandbox compilers integration, syntax parser, and mobile keyboard overlay.",
                            "Sprint 3" to "Neural AI Companion: Formulate Gemini api connections via Room chat history databases.",
                            "Sprint 4" to "Financial Ledger Core: Activate smart budget expense calculators with crypto blockchain monitors.",
                            "Sprint 5" to "Automator Triggers & IoT: Complete system macro shortcut builders and Zigbee smart device dashboards."
                        )

                        sprints.forEach { (title, desc) ->
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).background(NeonGreen))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(title.uppercase(), color = NeonGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                                Text(desc, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
                "wireframe" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("OMNI-CORE WIREFRAME CONCEPTS", color = NeonPink, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text("This wireframe visualizes our custom spatial modular layers. Our goal is absolute zero clutter.", color = Color.White, fontSize = 12.sp)

                        Text("HOME DASHBOARD SCHEMATIC", color = NeonCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("""
                            +-------------------------------------------+
                            | [HUD] TIME UTC | MULTI-DECK STREAMER (LP) |
                            +-------------------------------------------+
                            | [SYS STATUS] Welcome, Agent (State Card)  |
                            | [TABS] Chat Console   |   Ledger Wallet  |
                            +-------------------------------------------+
                            |                                           |
                            |  (Interactive viewport swaps dynamically)  |
                            |                                           |
                            +-------------------------------------------+
                            | [NAV RAIL] CoreHub | OmniIDE | Rules | Blue  |
                            +-------------------------------------------+
                        """.trimIndent(), color = CyberGrayText, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// Navigation buttons bottom rail
@Composable
fun NexusBottomNav(
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Top divider line for bottom bar
                drawLine(
                    color = NeonCyan.copy(alpha = 0.3f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f
                )
            },
        color = DarkSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple("core_hub", "Core Hub", Icons.Default.Home),
                Triple("ide", "OmniIDE", Icons.Default.Edit),
                Triple("automator", "Rules Engine", Icons.Default.Refresh),
                Triple("blueprint", "Manual", Icons.Default.Menu)
            )

            navItems.forEach { (screenId, label, icon) ->
                val isSelected = currentScreen == screenId
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onScreenSelected(screenId) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("nav_btn_$screenId"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) NeonCyan else CyberGrayText,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else CyberGrayText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
