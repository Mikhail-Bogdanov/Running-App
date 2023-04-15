package com.example.runningapp.events

sealed class Event {
    data class ShowSnackBar(val success: Boolean): Event()
    data class ShowToast(val success: Boolean): Event()
}