package com.example.hackthon_datallm_ai.geminidatamanager

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel(){
    private val _chatState = MutableStateFlow(ChatState())
    val     chatState = _chatState.asStateFlow()
    val isLoading = mutableStateOf(false)
    val responseMessage = mutableStateOf("")

    fun onEvent(event: ChatUIEvent){
        when(event){
            is ChatUIEvent.SendPrompt->{
                if(event.prompt.isNotEmpty()){
                    addPrompt(event.prompt)
                }
                getResponse(event.prompt)
            }

            is ChatUIEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String) {
        _chatState.update{
            it.copy(
                chatList = it.chatList.toMutableList().apply{
                    add(0, Chat(prompt,true))
                },
                prompt = ""
            )
        }
    }
    private fun getResponse(prompt: String){

        viewModelScope.launch {
            val chat = ChatData.getResponse(prompt,isLoading,responseMessage)
            _chatState.update{
                it.copy(
                    chatList = it.chatList.toMutableList().apply{
                        add(0,chat)
                    }
                )
            }
        }
    }
}