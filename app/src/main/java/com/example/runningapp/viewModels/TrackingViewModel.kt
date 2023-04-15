package com.example.runningapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.database.Run
import com.example.runningapp.events.Event
import com.example.runningapp.repository.RepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: RepositoryI
): ViewModel() {

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun insertRun(run: Run) = viewModelScope.launch {
        kotlin.runCatching {
            repository.insertRunLocal(run)
        }.onSuccess {
            successfulFeedback()
        }.onFailure {
            failureFeedback()
        }
    }

    private fun successfulFeedback(){
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackBar(true))
        }
    }

    private fun failureFeedback(){
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackBar(false))
        }
    }
}