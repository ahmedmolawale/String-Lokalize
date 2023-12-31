package com.ahmedmolawale.lokalize.states

sealed interface XMLProcessState {
    data class Error(val message: String) : XMLProcessState
    data class Success(val data: Map<String, String>) : XMLProcessState
}