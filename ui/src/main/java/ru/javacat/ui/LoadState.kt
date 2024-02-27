package ru.javacat.ui

sealed interface LoadState {
    object Loading: LoadState

    sealed interface Success: LoadState{
        object GoForward: Success

        object GoBack: Success
    }

    object Idle: LoadState

    data class Error(val message: String): LoadState
}