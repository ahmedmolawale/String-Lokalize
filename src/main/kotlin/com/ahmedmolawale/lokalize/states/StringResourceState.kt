package com.ahmedmolawale.lokalize.states

sealed interface StringResourceState {
    data class AlreadyExist(val message: String) : StringResourceState
    data class Success(val message: String) : StringResourceState
}