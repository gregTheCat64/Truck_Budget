package ru.javacat.ui

sealed interface LoadState {
    object Loading: LoadState

    object Success: LoadState

    object Idle: LoadState

    data class Error(val message: String): LoadState
}