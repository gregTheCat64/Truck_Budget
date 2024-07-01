package ru.javacat.ui

sealed interface LoadState {
    object Loading: LoadState

    sealed interface Success: LoadState{
        object OK: Success
        object GoForward: Success

        object GoBack: Success

        object Removed: Success

        object Created: Success
    }

    object Idle: LoadState

    data class Error(val message: String): LoadState
}