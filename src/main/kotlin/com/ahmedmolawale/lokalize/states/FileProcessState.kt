package com.ahmedmolawale.lokalize.states

sealed interface FileProcessState {
    data class Error(val message: String) : FileProcessState
    data class Success(val data: Map<String, StringBuilder>) : FileProcessState
}