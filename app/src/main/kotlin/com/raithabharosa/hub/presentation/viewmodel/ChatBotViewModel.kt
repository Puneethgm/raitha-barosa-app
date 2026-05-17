package com.raithabharosa.hub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raithabharosa.hub.data.repository.GrokRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ChatMessage(val sender: Sender, val text: String)
enum class Sender { USER, BOT }

class ChatBotViewModel(private val repo: GrokRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages
    private val systemPrompt = "You are RaithaBot, an agriculture assistant. Answer clearly and practically for all agriculture topics: crops, seeds, soil, fertilizers, irrigation, pests, diseases, weather impact, livestock, dairy, poultry, goat/sheep, fish farming, and farm economics. Keep replies concise and useful for farmers. If the question is unrelated to agriculture, respond: 'I can only help with agriculture-related questions.'"

    init {
        // Greeting from bot
        _messages.value = listOf(ChatMessage(Sender.BOT, "Hello! I am RaithaBot. Ask me about crops, livestock, poultry, dairy, fish farming, and other agriculture questions."))
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        _messages.value = _messages.value + ChatMessage(Sender.USER, text)
        viewModelScope.launch {
            val conversationHistory = _messages.value.takeLast(10).joinToString("\n") { msg ->
                if (msg.sender == Sender.USER) "User: ${msg.text}" else "Assistant: ${msg.text}"
            }
            val prompt = "$systemPrompt\n\n$conversationHistory"
            val res = withContext(Dispatchers.IO) { repo.send(prompt) }
            val reply = res.fold({ it }, { err -> "Error: ${err.toString()}" })
            _messages.value = _messages.value + ChatMessage(Sender.BOT, reply)
            val lower = reply.lowercase()
            if (lower.contains("sslhandshake") || lower.contains("tlsv1_alert") || lower.contains("unrecognized_name") || lower.contains("ssl")) {
                val diag = withContext(Dispatchers.IO) { repo.testConnectivity() }
                val diagText = diag.fold({ it }, { err -> "Diagnostic error: ${err.toString()}" })
                _messages.value = _messages.value + ChatMessage(Sender.BOT, "Connectivity diagnostic: $diagText")
            }
        }
    }
}
