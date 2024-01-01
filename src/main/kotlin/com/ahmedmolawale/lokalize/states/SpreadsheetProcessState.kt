package com.ahmedmolawale.lokalize.states

sealed interface SpreadsheetProcessState {
    data class Error(val message: String) : SpreadsheetProcessState
    data class Success(val data: Map<String, StringBuilder>) : SpreadsheetProcessState
}