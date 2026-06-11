package com.example.data

data class CodeFile(
    val name: String,
    val language: String,
    val content: String
)

data class ChatMessage(
    val sender: String, // "USER" or "NEXUS_AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ExpenseItem(
    val id: Int = 0,
    val description: String,
    val amount: Double,
    val category: String, // "CRYPTO", "FIAT", "FOOD", "SERVER"
    val isCrypto: Boolean = false
)

data class MacroRule(
    val id: String,
    val triggerName: String,
    val actionName: String,
    val isActive: Boolean
)

data class IoTDevice(
    val name: String,
    val room: String,
    val status: String,
    val isOn: Boolean
)
