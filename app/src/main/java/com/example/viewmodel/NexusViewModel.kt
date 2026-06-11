package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.GeminiContent
import com.example.api.GeminiPart
import com.example.api.GeminiRequest
import com.example.api.RetrofitClient
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NexusViewModel : ViewModel() {

    // --- Core UI Navigation ---
    private val _currentScreen = MutableStateFlow("core_hub")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screenId: String) {
        _currentScreen.value = screenId
        // Trigger macro if navigation matches trigger condition.
        checkNavigationMacro(screenId)
    }

    // --- IDE Module State ---
    private val _codeEditorContent = MutableStateFlow(
        "def compute_trajectory(quantum_particles):\n" +
        "    # Simulated Quantum Array Calculation\n" +
        "    flux = [p * 1.618 for p in quantum_particles]\n" +
        "    print(\"NexusOS Terminal: Flux trajectory calculations completed.\")\n" +
        "    return sum(flux)\n\n" +
        "res = compute_trajectory([1.2, 3.4, 5.6])\n" +
        "print(f\"Quantum Core output: {res}\")"
    )
    val codeEditorContent: StateFlow<String> = _codeEditorContent.asStateFlow()

    private val _codeLanguage = MutableStateFlow("Python")
    val codeLanguage: StateFlow<String> = _codeLanguage.asStateFlow()

    private val _terminalLogs = MutableStateFlow<List<String>>(
        listOf(
            "SYSTEM ROOT: v3.2.1-NexusOS initialized.",
            "CORE LOAD: 24% | STABLE",
            "O_S TERMINAL READY."
        )
    )
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    private val _isCompiling = MutableStateFlow(false)
    val isCompiling: StateFlow<Boolean> = _isCompiling.asStateFlow()

    private val _copilotSuggestion = MutableStateFlow<String?>("    # Suggestion: return round(sum(flux), 4)")
    val copilotSuggestion: StateFlow<String?> = _copilotSuggestion.asStateFlow()

    private val _isCopilotEnabled = MutableStateFlow(true)
    val isCopilotEnabled: StateFlow<Boolean> = _isCopilotEnabled.asStateFlow()

    fun updateCode(newCode: String) {
        _codeEditorContent.value = newCode
        // Dynamic Copilot prediction logic simulator
        if (_isCopilotEnabled.value) {
            _copilotSuggestion.value = when {
                newCode.endsWith(":\n") -> "    # Suggestion: pass\n"
                newCode.contains("import") -> "    # Suggestion: import numpy as np\n"
                newCode.contains("def ") -> "    # Suggestion:     return result\n"
                else -> null
            }
        } else {
            _copilotSuggestion.value = null
        }
    }

    fun setLanguage(lang: String) {
        _codeLanguage.value = lang
        _codeEditorContent.value = getSampleCode(lang)
    }

    fun toggleCopilot(enabled: Boolean) {
        _isCopilotEnabled.value = enabled
        if (!enabled) _copilotSuggestion.value = null
    }

    fun executeCode() {
        viewModelScope.launch {
            _isCompiling.value = true
            _terminalLogs.value = _terminalLogs.value + "[SANDBOX] Launching cloud secure runtime for ${codeLanguage.value}..."
            delay(1500) // simulated heavy cloud processing time compile
            _isCompiling.value = false
            
            val code = _codeEditorContent.value
            val resultLogs = mutableListOf<String>()
            resultLogs.add("---- EXECUTION STARTED COMPILE ----")

            // Smart code simulation execution helper
            if (code.contains("print")) {
                // parse print messages
                val lines = code.split("\n")
                lines.forEach { line ->
                    if (line.trim().startsWith("print(")) {
                        val insideText = line.substringAfter("print(").substringBeforeLast(")")
                            .replace("\"", "").replace("'", "")
                        resultLogs.add("> $insideText")
                    }
                }
            } else {
                resultLogs.add("> Code executed with no stream outputs.")
            }

            resultLogs.add("---- RETRO TERMINAL OUTPUT ----")
            resultLogs.add("Quantum sandbox compilation: OK")
            resultLogs.add("CPU Time: 0.08s | Power: 0.12W")
            resultLogs.add("Exit Code: 0 (SUCCESS)")
            _terminalLogs.value = _terminalLogs.value + resultLogs
        }
    }

    fun clearTerminal() {
        _terminalLogs.value = listOf("Retro IDE Workspace cleared.")
    }

    private fun getSampleCode(lang: String): String {
        return when (lang) {
            "Python" -> "def greet(system):\n    print(f\"Quantum greetings to {system}\")\n\ngreet(\"NexusOS User\")"
            "JavaScript" -> "const networkSpeed = 4800; \nconsole.log(`Analyzing telemetry: \${networkSpeed} Mbps`); \nif(networkSpeed > 1000) console.log('Holographic status: EXTREME');"
            "HTML/CSS" -> "<div class='glass-grid'>\n  <h1 style='color: rgb(0,255,204); text-shadow: 0 0 10px #00ffcc;'>Nexus Node</h1>\n  <p>Status: Synchronized - OmniCore</p>\n</div>"
            "C++" -> "#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << \"Compiling quantum main array...\" << endl;\n    return 0;\n}"
            else -> "print('Unknown source array')"
        }
    }

    // --- AI Companion Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("NEXUS_AI", "NexusOS core online. I am OmniCore, your voice & chat-driven intelligence companion. Let's create something extraordinary. Ask me to write code, review your budget, or build a system macro!"),
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.trim().isEmpty()) return
        val userMsg = ChatMessage("USER", userText)
        _chatMessages.value = _chatMessages.value + userMsg

        _isAiTyping.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val key = BuildConfig.GEMINI_API_KEY
            // If API key is empty or placeholder, fallback to an extremely polished interactive local rule-based intelligence
            if (key.isEmpty() || key == "MY_GEMINI_API_KEY" || key.contains("PLACEHOLDER") || key == "GEMINI_API_KEY") {
                delay(1200) // Simulated cognitive latency
                val aiReply = runLocalCompanionResponse(userText)
                _chatMessages.value = _chatMessages.value + ChatMessage("NEXUS_AI", aiReply)
                _isAiTyping.value = false
            } else {
                try {
                    val promptWithContext = "You are OmniCore, the cybernetic Super-App companion inside NexusOS. Keep responses concise, direct, helpful, and in raw markdown text with cyberpunk terminal aesthetic style. User query: $userText"
                    val req = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(promptWithContext)))))
                    val res = RetrofitClient.service.generateContent(key, req)
                    val reply = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                        ?: "No dynamic holographic answer received from neural array."
                    _chatMessages.value = _chatMessages.value + ChatMessage("NEXUS_AI", reply)
                } catch (e: Exception) {
                    val fallback = "Local neural link engaged due to network interference/key configuration. Error: ${e.localizedMessage}. Fallback: ${runLocalCompanionResponse(userText)}"
                    _chatMessages.value = _chatMessages.value + ChatMessage("NEXUS_AI", fallback)
                } finally {
                    _isAiTyping.value = false
                }
            }
        }
    }

    private fun runLocalCompanionResponse(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi ") || lower.contains("hey") -> {
                "Grid systems nominal. Welcome back, agent. How shall we optimize your node operations today?"
            }
            lower.contains("ide") || lower.contains("code") || lower.contains("write") -> {
                "Ready to write code. Navigate to the **OmniEditor IDE tab** on your system bar. I can simulate multiple languages (Python, C++, Java) and compile them locally inside our cloud sandbox pipeline."
            }
            lower.contains("wallet") || lower.contains("expense") || lower.contains("money") || lower.contains("budget") -> {
                val currentExpenses = _expenses.value.joinToString("\n") { "• ${it.category}: ${it.description} -> $${it.amount}" }
                "Retrieving financial ledger...\n\n**Financial Node Status:**\n- Fiat Liquidity: $${String.format("%.2f", _fiatBalance.value)}\n- Crypto Assets: 0.12 BTC / 1.4 ETH (~$${String.format("%.2f", _cryptoBalance.value)})\n\n**Recent Ledger Lines:**\n$currentExpenses\n\nTo insert a transaction, type: 'add expense [category] [description] [amount]' e.g. *add expense food synth burger 15*"
            }
            lower.contains("add expense") -> {
                // Parse e.g. "add expense food sushi 18"
                try {
                    val parts = input.split(" ")
                    val cat = parts.getOrNull(2)?.uppercase() ?: "MISC"
                    val desc = parts.getOrNull(3) ?: "Aux Transaction"
                    val amtStr = parts.getOrNull(4) ?: "10"
                    val amt = amtStr.toDoubleOrNull() ?: 10.0
                    
                    val cryptoFlag = cat == "CRYPTO"
                    val newExp = ExpenseItem(_expenses.value.size + 1, desc, amt, cat, cryptoFlag)
                    
                    _expenses.value = _expenses.value + newExp
                    if (cryptoFlag) {
                        _cryptoBalance.value -= amt
                    } else {
                        _fiatBalance.value -= amt
                    }
                    "Ledger synchronized correctly! Record entered: \n- Category: **$cat**\n- Details: *$desc*\n- Amount: **$$amt**\n\nYour balances have been deducted in the secure core ledger database."
                } catch (e: Exception) {
                    "Protocol failure: Could not parse expense details. Format: 'add expense [cat] [details] [amount]'"
                }
            }
            lower.contains("macro") || lower.contains("automate") -> {
                "Macro engine scanning positive. Registered tasks active:\n- DND Sync: ${_macros.value.find { it.id == "ide_dnd" }?.isActive}\n- Cooling optimization: ${_macros.value.find { it.id == "cpu_cooling" }?.isActive}\n"
            }
            else -> {
                "Direct telemetry analysis: Omnivore node parsed '${input}'. I have logged this request in your terminal log. Let's build, compile files, automate hardware, or manage financial ledger assets."
            }
        }
    }

    // --- Smart Wallet State ---
    private val _fiatBalance = MutableStateFlow(1850.25)
    val fiatBalance: StateFlow<Double> = _fiatBalance.asStateFlow()

    private val _cryptoBalance = MutableStateFlow(4120.40)
    val cryptoBalance: StateFlow<Double> = _cryptoBalance.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseItem>>(
        listOf(
            ExpenseItem(1, "Server Cloud Hosting Instance", 45.00, "SERVER"),
            ExpenseItem(2, "Sub-orbit Courier", 12.50, "TRANSPORT"),
            ExpenseItem(3, "NeoTokyo Ramen synth", 14.20, "FOOD"),
            ExpenseItem(4, "0.002 BTC Hardware Nodes", 120.00, "CRYPTO", true)
        )
    )
    val expenses: StateFlow<List<ExpenseItem>> = _expenses.asStateFlow()

    fun addExpenseDirectly(desc: String, amt: Double, cat: String, isCrypto: Boolean) {
        val newId = _expenses.value.size + 1
        val item = ExpenseItem(newId, desc, amt, cat, isCrypto)
        _expenses.value = _expenses.value + item
        if (isCrypto) {
            _cryptoBalance.value -= amt
        } else {
            _fiatBalance.value -= amt
        }
    }

    // --- Automation Macro & IOT state ---
    private val _macros = MutableStateFlow<List<MacroRule>>(
        listOf(
            MacroRule("ide_dnd", "When IDE Screen is opened", "Activate Do Not Disturb + play Lo-Fi soundtrack", true),
            MacroRule("cpu_cooling", "When terminal compiling is active", "Increase CPU cooling, limit notification frequency", true)
        )
    )
    val macros: StateFlow<List<MacroRule>> = _macros.asStateFlow()

    private val _iotDevices = MutableStateFlow<List<IoTDevice>>(
        listOf(
            IoTDevice("Ambient Neon Tube", "Living Grid", "Pulse Cyan", true),
            IoTDevice("Dynamic Server Fan", "Node Server Corner", "Auto Max", true),
            IoTDevice("Hi-Fi Sound Array", "Living Grid", "lofi active", false)
        )
    )
    val iotDevices: StateFlow<List<IoTDevice>> = _iotDevices.asStateFlow()

    fun toggleMacro(id: String) {
        _macros.value = _macros.value.map {
            if (it.id == id) it.copy(isActive = !it.isActive) else it
        }
    }

    fun toggleIoT(name: String) {
        _iotDevices.value = _iotDevices.value.map {
            if (it.name == name) it.copy(isOn = !it.isOn) else it
        }
    }

    private val _macroNotification = MutableStateFlow<String?>(null)
    val macroNotification: StateFlow<String?> = _macroNotification.asStateFlow()

    fun clearNotification() {
        _macroNotification.value = null
    }

    private fun checkNavigationMacro(screenId: String) {
        if (screenId == "ide") {
            val ideDnd = _macros.value.find { it.id == "ide_dnd" }
            if (ideDnd?.isActive == true) {
                _macroNotification.value = "Nexus Rule Executed: Opened IDE -> Do Not Disturb ON + Cyber-lofi stream enabled."
                _isAudioPlaying.value = true // automatically starts lo-fi player
            }
        }
    }

    // --- Music Stream / LO-FI State ---
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _currentTrack = MutableStateFlow("Vapor Synth Nodes - Track 04")
    val currentTrack: StateFlow<String> = _currentTrack.asStateFlow()

    private val _audioTrackProgress = MutableStateFlow(0.35f)
    val audioTrackProgress: StateFlow<Float> = _audioTrackProgress.asStateFlow()

    fun toggleAudio() {
        _isAudioPlaying.value = !_isAudioPlaying.value
    }

    fun updateProgress(value: Float) {
        _audioTrackProgress.value = value
    }
}
