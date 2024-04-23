package com.example.hackthon_datallm_ai.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackthon_datallm_ai.geminidatamanager.ChatUIEvent
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelChat(private val chatviewModel: ChatViewModel) : ViewModel() {
    private var _database =""
    val database:String
        get() {
         return  _database
        }
    fun setdatabase(databsae:String){
        _database=database
    }
    val conversation: StateFlow<List<ChatUiModel.Message>>
        get() = _conversation
    private val _conversation = MutableStateFlow(
        listOf(ChatUiModel.Message.initConv)
    )
    fun sendChat(msg: String) {

        val myChat = ChatUiModel.Message(msg, ChatUiModel.Author.me)
        viewModelScope.launch {
            _conversation.emit(_conversation.value + myChat)

            delay(1000)
            _conversation.emit(_conversation.value + getbotreply(msg)) // make function to get bot reply

        }
    }
    private suspend fun getbotreply(msg: String): ChatUiModel.Message {
        chatviewModel.onEvent(ChatUIEvent.UpdatePrompt(msg))
        chatviewModel.onEvent(ChatUIEvent.SendPrompt(msg))
        delay(3000)

        return ChatUiModel.Message(
            text = chatviewModel.responseMessage.value,
            author = ChatUiModel.Author.bot
        )
    }
}